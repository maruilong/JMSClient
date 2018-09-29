package com.xinyuan.comm;

import lombok.Data;

/**
 * 接受文件消息之后的类
 *
 * @author liang
 */
@Data
public class RecBean {

    private Long msgId;

    private Long lastId;

    private String fileSavePath;

    private String fullName;

    private String sender;

    private Integer fileType;

    /**
     * 收到状态
     */
    private Integer fileRev;
}
