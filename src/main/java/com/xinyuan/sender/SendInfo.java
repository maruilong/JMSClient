package com.xinyuan.sender;

import com.xinyuan.comm.InitSend;
import com.xinyuan.comm.MessageBak;
import com.xinyuan.comm.SendBean;
import com.xinyuan.comm.enums.MessageType;
import com.xinyuan.comm.enums.UploadState;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.Configuration;
import com.xinyuan.message.FileMessage;
import com.xinyuan.message.MessageInfo;
import com.xinyuan.sendout.SendHelper;
import com.xinyuan.service.UploadService;
import com.xinyuan.util.FileCopy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * 消息发送模块
 *
 * @author Vic
 */
@Slf4j
@Component
public class SendInfo {

    @Autowired
    private FileSender fileSender;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private SendHelper sendHelper;

    /**
     * 发送文件消息
     *
     * @param bean 发送消息bean类
     * @throws Exception
     */
    public void sendFileInfo(SendBean bean) throws Exception {

        //分割文件夹名称：接收端~消息id~上一个消息id~文件类型
        String splitFolderName = bean.getReceiver() + "~" + bean.getTaskId() + "~" + bean.getLastFileId() + "~" + bean.getFileType();
        String splitPath = clientConfig.getWorkDirUpload() + Configuration.spliteFileDir + splitFolderName;
        FileCopy.createFile(splitPath, false);
        //切割文件
        String[] fileList = sendHelper.handleFile(bean.getReceiver(), bean.getFilePath() + bean.getFileName(), bean.getTaskId(), splitFolderName);
        int blockNum = fileList.length;
        if (fileList != null) {
            int i = 1;
            while (true) {
                // 循环每一个文件
                if (i <= blockNum) {
                    File sFile = new File(fileList[i - 1]);
                    sendFileMessage(sFile, blockNum, i, bean.getReceiver(), bean.getTaskId(), bean.getLastFileId(), fileList[i - 1], bean.getFileType());
                    sFile.delete();
                    Thread.sleep(500);
                    //判断如果全部发送完毕则删除文件夹
                    if (i == blockNum) {
                        File splitDir = new File(splitPath);
                        splitDir.delete();
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 设置消息，发送单个文件
     *
     * @param file      要发送的文件
     * @param fileNum   分文件个数
     * @param fileCount 切割的第几个文件
     * @param receiver  接受者
     * @param msgId     消息id
     * @param lastId    上一个消息的id
     * @param filepath  单个分文件路径
     * @param fileType  文件类型
     * @throws Exception
     */
    public void sendFileMessage(File file, int fileNum, int fileCount, String receiver,
                                Long msgId, Long lastId, String filepath, Integer fileType) throws Exception {
        long fileLength = file.length();
        byte[] buf = new byte[(int) fileLength];
        InputStream inStream = new FileInputStream(file);
        int pos = 0;
        do {
            int byteRead = inStream.read(buf, pos, (int) fileLength - pos);

            pos += byteRead;
        } while (pos < fileLength);

        inStream.close();

        // 逐一设定所要发送的文件类型消息的各个字段值
        FileMessage fileMessage = new FileMessage();
        fileMessage.setMsgId(msgId);
        fileMessage.setLastId(lastId);
        fileMessage.setSender(clientConfig.getCompanyId());
        fileMessage.setReceiver(receiver);
        fileMessage.setFileContent(buf);
        fileMessage.setFileName(filepath.substring(filepath.lastIndexOf(File.separator) + 1));
        fileMessage.setMD5(file.getName().split("_")[1]);
        fileMessage.setFileNum(fileNum);
        fileMessage.setFileCount(fileCount);
        fileMessage.setFileType(fileType);

        while (true) {
            try {
                String uuid = UUID.randomUUID().toString();
                fileSender.send(uuid, fileMessage);

                //发送成功之后,修改本地状态
                uploadService.updateState(msgId, clientConfig.getCompanyId(), UploadState.SENT.getState());
                break;
            } catch (Exception e) {
                //捕捉发送消息时的异常，是否失败，失败则重发
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化，主要作用为发送上一次中断发送的文件，断点续传功能
     *
     * @return String[][] 消息信息
     * String[0][0] 消息id
     * String[0][1] 接收端
     */
    public List<InitSend> sendInit() {

        List<InitSend> returnValue = new ArrayList<>();
        boolean flag = false;
        File dir = new File(clientConfig.getWorkDirUpload() + Configuration.spliteFileDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File[] files = dir.listFiles();
        if (files.length == 0) {
            return returnValue;
        }
        for (int i = 0; i < files.length; ++i) {
            try {
                File file = files[i];
                String[] list = file.list();
                if (list != null && list.length > 0) {
                    InitSend initSend = new InitSend();

                    String filepath = file.getAbsolutePath();
                    String[] fInfo = file.getName().split("~");
                    String receiver = fInfo[0];
                    Long msgId = Long.parseLong(fInfo[1]);
                    Long lastId = Long.parseLong(fInfo[2]);
                    Integer fileType = Integer.parseInt(fInfo[3]);

                    // 对这些文件文件进行排序，按顺序发送
                    Arrays.sort(list, com);
                    for (int j = 0; j < list.length; ++j) {
                        String innerFilePath = filepath + File.separator + list[j];
                        File innerFile = new File(innerFilePath);
                        String[] info = innerFile.getName().split("_");
                        this.sendFileMessage(innerFile, Integer.parseInt(info[2]), Integer.parseInt(info[3]), receiver, msgId, lastId, innerFilePath, fileType);
                        innerFile.delete();
                    }
                    if (file.list().length == 0) {
                        file.delete();
                    }
                    initSend.setTaskId(msgId);
                    initSend.setCompanyId(receiver);

                    flag = true;
                }
            } catch (Exception e) {
                log.error(returnValue.toString());
                e.printStackTrace();
            }
        }
        if (flag) {
            return returnValue;
        } else {
            return null;
        }
    }


    /**
     * 发送字符消息
     */
    public void sendMsgInfo(MessageBak messageBak) {
        MessageInfo messageInfo = new MessageInfo();

        // 文件是否接受成功
        if (MessageType.DOWNLOADED.getType().equals(messageBak.getMsgType())) {
            messageInfo.setFile(messageBak.getRecOrExec());
            messageInfo.setMsgType(MessageType.DOWNLOADED.getType());
            log.info(messageBak.toString());
        } else { // 执行是否成功
            messageInfo.setExecute(messageBak.getRecOrExec());
            messageInfo.setMsgType(MessageType.EXECUTED.getType());
            log.info(messageInfo.toString());
        }
        messageInfo.setMsgId(messageBak.getMsgId());
        messageInfo.setFrom(clientConfig.getCompanyId());
        messageInfo.setTo(messageBak.getReceiver());
        // 发送消息
        messageSender.send(UUID.randomUUID().toString(), messageInfo);
    }

    Comparator<String> com = (o1, o2) -> {
        // 前面3个IF主要是判空的
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }
        // 这里没有做太多的判断, index 代表第几个开始是数字, 直接从后面遍历
        // 比如 aa11, 我们就会判断从下标[2]开始为不是数字, 就直接截取 [2] 后面, 即11

        int index = 0;
        for (index = o1.length() - 1; index >= 0
                && (o1.charAt(index) >= '0' && o1.charAt(index) <= '9'); index--)
            ;
        int num1 = Integer.parseInt(o1.substring(index + 1));

        for (index = o2.length() - 1; index >= 0
                && (o2.charAt(index) >= '0' && o2.charAt(index) <= '9'); index--)
            ;
        int num2 = Integer.parseInt(o2.substring(index + 1));
        return num1 - num2;
    };
}