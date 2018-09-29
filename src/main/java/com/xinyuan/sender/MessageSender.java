package com.xinyuan.sender;

import com.xinyuan.config.QueueConfig;
import com.xinyuan.config.RabbitMqConfig;
import com.xinyuan.message.MessageInfo;
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
public class MessageSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private QueueConfig queueConfig;

    /**
     * 发送消息
     *
     * @param uuid
     * @param message 消息
     */
    public void send(String uuid, MessageInfo message) {
        CorrelationData correlationId = new CorrelationData(uuid);
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, queueConfig.msgSendQueue().getName(), message, correlationId);
    }
}
