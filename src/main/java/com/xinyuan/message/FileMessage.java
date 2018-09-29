package com.xinyuan.message;

import lombok.Data;

/**
 * @author liang
 */
@Data
public class FileMessage {

    private Long msgId;

    private Long lastId;

    private String sender;

    private String receiver;

    private byte[] fileContent;

    private String fileName;

    private String MD5;

    private Integer fileNum;

    private Integer fileCount;

    private Integer fileType;

}
