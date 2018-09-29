package com.xinyuan.receiver.message;

import com.rabbitmq.client.Channel;
import com.xinyuan.comm.enums.MessageType;
import com.xinyuan.comm.enums.UploadState;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.RabbitMqConfig;
import com.xinyuan.message.MessageInfo;
import com.xinyuan.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageConsumer {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ClientConfig clientConfig;

    @RabbitListener(queues = {RabbitMqConfig.MSG_QUEUE + "_" + "${client.company-id}"})
    public void handMessage(MessageInfo messageInfo, Channel channel) throws Exception {

        try {
            Long msgId = messageInfo.getMsgId();
            Integer msgType = messageInfo.getMsgType();
            //拿到消息ID 判断消息类型

            if (MessageType.DOWNLOADED.getType().equals(msgType)) {
                uploadService.updateState(msgId, clientConfig.getCompanyId(), UploadState.DOWNLOADED.getState());
            } else {
                uploadService.updateState(msgId, clientConfig.getCompanyId(), UploadState.EXECUTED.getState());
            }

        } catch (Exception e) {
            channel.basicReject(1, false);
            log.error(e.getMessage());
            log.error(messageInfo.toString());
        }
    }
}
