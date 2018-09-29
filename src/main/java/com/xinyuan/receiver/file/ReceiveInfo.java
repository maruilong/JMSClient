package com.xinyuan.receiver.file;

import com.xinyuan.comm.RecBean;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.Configuration;
import com.xinyuan.config.RabbitMqConfig;
import com.xinyuan.message.FileMessage;
import com.xinyuan.util.HashFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * @author liang
 */
@Slf4j
@Component
public class ReceiveInfo {

    @Autowired
    private ClientConfig clientConfig;

    public RecBean recFileInfo(FileMessage fileMessage) throws Exception {
        RecBean returnValue = new RecBean();

        Integer fileNum = fileMessage.getFileNum();
        Integer fileCount = fileMessage.getFileCount();
        String md5 = fileMessage.getMD5();

        // 文件名的各个字段为"文件名称.zip_md5_(file_nums)_(file_count)
        // 传送文件名按"_"切割
        String[] s = fileMessage.getFileName().split("_");
        // 第i个文件的文件名
        String filename_i = s[0] + "_" + s[1] + "_" + fileMessage.getSender() + "_" + fileMessage.getReceiver() + "_" + fileMessage.getMsgId();
        String filePath = clientConfig.getWorkDirDownload() + Configuration.fileSave_temp + fileMessage.getSender() + "~" + fileMessage.getMsgId() + "~" + fileMessage.getLastId() + "~" + fileMessage.getFileType() + File.separator;
        // 分文件的保存路径
        String singleFilePath = filePath + filename_i + "_" + fileMessage.getFileCount();
        // 原文件名保存为“文件名.zip”，去除“.zip”,还原成原文件名
        // fullname压缩后的文件名，即分文件合成后的文件名
        String fullName = s[0];
        if (!(new File(filePath).isDirectory())) {
            new File(filePath).mkdir();
        }

        // 写单个分文件
        OutputStream outStream = new FileOutputStream(singleFilePath);
        outStream.write(fileMessage.getFileContent());
        outStream.close();

        Integer fileRec = -1;

        // 当接收到的文件等于总的切割后文件数
        if (fileNum.equals(fileCount)) {
            // 合成文件
            combineFile(filePath, fullName, fileNum, filename_i);
            // 取md5，并且比较
            String fileSavePath = clientConfig.getWorkDirDownload() + Configuration.save_destination;
            String MD5File = HashFile.checkFile(fileSavePath + fullName);
            if (MD5File.intern() == md5.intern()) {

                // md5一致,成功
                fileRec = 1;

                // 删除临时文件夹下的文件和目录
                File sFile = new File(filePath);
                File[] file = sFile.listFiles();
                for (int i = 0; i < file.length; ++i) {
                    file[i].delete();
                }
                sFile.delete();

            } else {
                //md5不一致，验证失败
                log.error("receive file MD5 check :"
                        + fileSavePath + fullName + " fail"
                        + "  " + "msg_id:" + fileMessage.getMsgId() + "  " + "sender:"
                        + fileMessage.getSender() + "  " + "receiver:" + fileMessage.getReceiver());
            }

            returnValue.setMsgId(fileMessage.getMsgId());
            returnValue.setLastId(fileMessage.getLastId());
            returnValue.setFileSavePath(fileSavePath);
            returnValue.setFullName(fullName);
            returnValue.setSender(fileMessage.getSender());
            returnValue.setFileType(fileMessage.getFileType());
            returnValue.setFileRev(fileRec);
        }
        return returnValue;
    }


    /**
     * 合并多个文件成一个文件
     *
     * @param file           :destination of combination of seperate files
     * @param fileNameNoPath :such as "1.txt" in /root/1.txt
     * @param fileNums       seperate file numbers
     * @param filename_i
     */
    public void combineFile(String file, String fileNameNoPath,
                            int fileNums, String filename_i) {
        long alreadyWrite = 0;
        FileInputStream fis = null;
        int len = 0;
        byte[] bt = new byte[1024];
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(clientConfig.getWorkDirDownload() + Configuration.save_destination
                    + fileNameNoPath, "rw");
            for (int i = 1; i <= fileNums; i++) {
                File seperatefile = new File(file + File.separator + filename_i + "_" + i);
                if (seperatefile.exists()) {
                    raf.seek(alreadyWrite);
                    fis = new FileInputStream(seperatefile);
                    while ((len = fis.read(bt)) > 0) {
                        raf.write(bt, 0, len);
                    }
                    fis.close();
                    alreadyWrite = alreadyWrite + seperatefile.length();
                } else {
                    log.error("seperate file " + i + ":" + seperatefile
                            + "doesn't exist");
                    break;
                }

            }
            raf.close();
        } catch (Exception e) {
            // 这里处理的内容为合并文件的异常
            log.error(e.getMessage());
            e.printStackTrace();
            try {
                if (raf != null) {
                    raf.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException g) {
                g.printStackTrace();
            }
        }
    }

}
