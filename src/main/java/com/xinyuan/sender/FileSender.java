package com.xinyuan.sender;

import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.QueueConfig;
import com.xinyuan.config.RabbitMqConfig;
import com.xinyuan.message.FileMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liang
 */
@Slf4j
@Component
public class FileSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private QueueConfig queueConfig;

    /**
     * 发送消息
     *
     * @param uuid
     * @param fileMessage 消息
     */
    public void send(String uuid, FileMessage fileMessage) {
        CorrelationData correlationId = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, queueConfig.fileSendQueue().getName(), fileMessage, correlationId);
    }
}