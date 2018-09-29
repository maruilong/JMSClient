package com.xinyuan.comm;

import lombok.Data;

/**
 * @author liang
 */
@Data
public class SendBean {

    /**
     * 接收者
     */
    private String receiver;
    /**
     * 消息id，发送方产生的唯一标识
     */
    private Long taskId;
    /**
     * 发送方上一文件的唯一标识
     */
    private Long lastFileId;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 文件类型
     */
    private Integer fileType;

    public SendBean() {
    }

    public SendBean(String receiver, Long taskId, Long lastFileId, String fileName, String filePath, Integer fileType) {
        this.receiver = receiver;
        this.taskId = taskId;
        this.lastFileId = lastFileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }
}
