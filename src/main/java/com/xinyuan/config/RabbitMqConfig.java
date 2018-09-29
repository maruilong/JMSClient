package com.xinyuan.config;

import com.xinyuan.mqcallback.MsgSendConfirmCallBack;
import com.xinyuan.mqcallback.MsgSendReturnCallback;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liang
 */
@Configuration
public class RabbitMqConfig {


    /**
     * 消息交换机的名字
     */
    public static final String EXCHANGE = "exchange";
    /**
     * 队列名称
     */
    public static final String FILE_QUEUE = "file_queue";

    public static final String MSG_QUEUE = "msg_queue";


    /*
     *
     * key: queue在该direct-exchange中的key值，当消息发送给direct-exchange中指定key为设置值时，
     *   消息将会转发给queue参数指定的消息队列
     */

    /**
     *
     */
    @Autowired
    private QueueConfig queueConfig;

    @Autowired
    private ExchangeConfig exchangeConfig;

    /**
     * 将消息队列1和交换机进行绑定
     */
    @Bean
    public Binding bindingFileSend() {
        return BindingBuilder.bind(queueConfig.fileSendQueue()).to(exchangeConfig.directExchange()).withQueueName();
    }

    /**
     * 将消息队列2和交换机进行绑定
     */
    @Bean
    public Binding bindingMsgSend() {
        return BindingBuilder.bind(queueConfig.msgSendQueue()).to(exchangeConfig.directExchange()).withQueueName();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new ContentTypeDelegatingMessageConverter(new Jackson2JsonMessageConverter());
    }

    /**
     * 自定义rabbit template用于数据的接收和发送
     * 可以设置消息确认机制和回调
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        可以自定义消息转换器  默认使用的JDK的，所以消息对象需要实现Serializable
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        /**若使用confirm-callback或return-callback，
         * 必须要配置publisherConfirms或publisherReturns为true
         * 每个rabbitTemplate只能有一个confirm-callback和return-callback
         */
        template.setConfirmCallback(msgSendConfirmCallBack());

        /**
         * 使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true，
         * 可针对每次请求的消息去确定’mandatory’的boolean值，
         * 只能在提供’return -callback’时使用，与mandatory互斥
         */
        template.setReturnCallback(msgSendReturnCallback());
        template.setMandatory(true);
        return template;
    }


    /*  关于 msgSendConfirmCallBack 和 msgSendReturnCallback 的回调说明：
        1.如果消息没有到exchange,则confirm回调,ack=false
        2.如果消息到达exchange,则confirm回调,ack=true
        3.exchange到queue成功,则不回调return
        4.exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
    */

    /**
     * 消息确认机制
     * Confirms给客户端一种轻量级的方式，能够跟踪哪些消息被broker处理，
     * 哪些可能因为broker宕掉或者网络失败的情况而重新发布。
     * 确认并且保证消息被送达，提供了两种方式：发布确认和事务。(两者不可同时使用)
     * 在channel为事务时，不可引入确认模式；同样channel为确认模式下，不可使用事务。
     *
     * @return
     */
    @Bean
    public MsgSendConfirmCallBack msgSendConfirmCallBack() {
        return new MsgSendConfirmCallBack();
    }

    @Bean
    public MsgSendReturnCallback msgSendReturnCallback() {
        return new MsgSendReturnCallback();
    }


}
