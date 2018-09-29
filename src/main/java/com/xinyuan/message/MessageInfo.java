package com.xinyuan.message;

import lombok.Data;

/**
 * @author liang
 */
@Data
public class MessageInfo {


    /**
     * 消息ID
     */
    private Long msgId;

    /**
     * 消息类型
     * *                  1 发送端收到文件后返回的状态（反馈）
     * *                  2 发送端执行文件后返回的状态（反馈）
     * *                  3 发送端发送文件成功后更改状态
     * *                  4 接收端成功接收文件后更改状态
     */
    private Integer msgType;

    /**
     * 文件接收或执行是否成功 1 成功 0 失败
     */
    private String file;

    /**
     * 是否执行成功
     */
    private String execute;

    private String from;

    private String to;

}
