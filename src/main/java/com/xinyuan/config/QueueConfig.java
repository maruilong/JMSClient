package com.xinyuan.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liang
 */
@Configuration
public class QueueConfig {

    @Autowired
    private ClientConfig clientConfig;

    /**
     * 文件消息发送队列
     *
     * @return
     */
    @Bean
    public Queue fileSendQueue() {
        /**
         durable="true" 持久化 rabbitmq重启的时候不需要创建新的队列
         auto-delete 表示消息队列没有在使用时将被自动删除 默认是false
         exclusive  表示该消息队列是否只在当前connection生效,默认是false
         */
        System.out.println("发送者:" + RabbitMqConfig.FILE_QUEUE + "_" + clientConfig.getReceiveId());
        return new Queue(RabbitMqConfig.FILE_QUEUE + "_" + clientConfig.getReceiveId(), true, false, false);
    }

    /**
     * 文件消息接受队列
     *
     * @return
     */
    @Bean
    public Queue fileReceiveQueue() {
        System.out.println("接受者:" + RabbitMqConfig.FILE_QUEUE + "_" + clientConfig.getCompanyId());

        return new Queue(RabbitMqConfig.FILE_QUEUE + "_" + clientConfig.getCompanyId(), true, false, false);
    }

    /**
     * 字符消息发送队列
     *
     * @return
     */
    @Bean
    public Queue msgSendQueue() {
        System.out.println("发送者:" + RabbitMqConfig.MSG_QUEUE + "_" + clientConfig.getReceiveId());
        return new Queue(RabbitMqConfig.MSG_QUEUE + "_" + clientConfig.getReceiveId(), true, false, false);
    }

    /**
     * 字符消息接受队列
     *
     * @return
     */
    @Bean
    public Queue msgReceiveQueue() {
        System.out.println("接受者:" + RabbitMqConfig.MSG_QUEUE + "_" + clientConfig.getCompanyId());
        return new Queue(RabbitMqConfig.MSG_QUEUE + "_" + clientConfig.getCompanyId(), true, false, false);
    }

}
