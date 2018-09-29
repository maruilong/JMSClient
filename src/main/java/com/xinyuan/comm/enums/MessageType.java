package com.xinyuan.comm.enums;


/**
 * @author liang
 */

public enum MessageType {


    /**
     * 下载完成发送消息
     */

    DOWNLOADED(1, "已下载"),


    /**
     * 执行完成发送消息
     */
    EXECUTED(2, "已执行");

    private Integer type;

    private String value;

    public MessageType getByState(Integer type) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.type.equals(type)) {
                return messageType;
            }
        }
        return DOWNLOADED;
    }

    MessageType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
