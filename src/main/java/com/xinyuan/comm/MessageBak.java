package com.xinyuan.comm;

import lombok.Data;

/**
 * @author liang
 */
@Data
public class MessageBak {

    /**
     * 消息ID
     */
    private Long msgId;

    /**
     * 接受端
     */
    private String receiver;

    /**
     * 消息类型
     * *
     * *    1:接受端接受消息
     * *
     * *
     */
    private Integer msgType;

    /**
     * 文件接收或执行是否成功 1 成功 0 失败
     */
    private String recOrExec;

    @Override
    public String toString() {
        return "MessageBak{" +
                "msgId='" + msgId + '\'' +
                ", receiver='" + receiver + '\'' +
                ", msgType='" + msgType + '\'' +
                ", recOrExec='" + recOrExec + '\'' +
                '}';
    }
}
