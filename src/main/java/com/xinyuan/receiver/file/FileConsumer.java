package com.xinyuan.receiver.file;

import com.rabbitmq.client.Channel;
import com.xinyuan.comm.MessageBak;
import com.xinyuan.comm.RecBean;
import com.xinyuan.comm.enums.DownloadState;
import com.xinyuan.comm.enums.MessageType;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.RabbitMqConfig;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.message.FileMessage;
import com.xinyuan.sender.SendInfo;
import com.xinyuan.service.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author liang
 */
@Slf4j
@Component
public class FileConsumer {

    @Autowired
    private ReceiveInfo receiveInfo;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private SendInfo sendInfo;

    /**
     * 接受文件消息
     *
     * @param fileMessage
     */
    @RabbitListener(queues = {RabbitMqConfig.FILE_QUEUE + "_" + "${client.company-id}"})
    public void handleMessage(FileMessage fileMessage, Channel channel) throws Exception {
        try {

            RecBean recBean = receiveInfo.recFileInfo(fileMessage);

            Integer fileRev = recBean.getFileRev();
            /**
             * 验证通过
             */
            if (1 == fileRev) {
                //保存下载端信息
                DownloadInfo downloadInfo = new DownloadInfo();

                downloadInfo.setFileId(recBean.getMsgId());
                downloadInfo.setLastId(recBean.getLastId());
                downloadInfo.setFilePath(recBean.getFileSavePath());
                downloadInfo.setFileName(recBean.getFullName());
                downloadInfo.setCompanyId(recBean.getSender());
                downloadInfo.setDownloadTime(new Date());
                downloadInfo.setType(recBean.getFileType());
                downloadInfo.setState(DownloadState.DOWNLOADED.getState());

                try {
                    DownloadInfo save = downloadService.save(downloadInfo);

                    if (save != null) {
                        MessageBak messageBak = new MessageBak();
                        messageBak.setMsgId(fileMessage.getMsgId());
                        messageBak.setMsgType(MessageType.DOWNLOADED.getType());
                        messageBak.setReceiver(fileMessage.getSender());
                        messageBak.setRecOrExec("1");
                        sendInfo.sendMsgInfo(messageBak);
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }
            }
        } catch (Exception e) {
            channel.basicReject(1, true);
            log.error(e.getMessage());
            log.error(fileMessage.toString());
        }

    }
}
