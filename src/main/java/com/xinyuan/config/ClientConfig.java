package com.xinyuan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author liang
 */
@Data
@Component
public class ClientConfig {

    /**
     * 发送者ID
     */
    @Value("${client.company-id}")
    private String companyId;

    /**
     * 接受者ID
     */
    @Value("${client.receive-id}")
    private String receiveId;

    private Long sendWaitTime;

    @Value("${client.bak-dir}")
    private String bakDir;

    @Value("${client.work-dir-upload}")
    private String workDirUpload;

    @Value("${client.work-dir-download}")
    private String workDirDownload;

    @Value("${client.attach-dir-download}")
    private String attachDirDownload;

    @Value("${client.history-dir-download}")
    private String historyDirDownload;

    @Value("${client.local-oracle-home}")
    private String localOracleHome;

    /**
     * 消息备份文件路径
     */
    private String msgBakPath;
    /**
     * 数据库备份文件路径
     */
    private String dbBakPath;


    public String getMsgBakPath() {
        return bakDir + "msg_bak.bak";
    }

    public String getDbBakPath() {
        return bakDir + "db_bak.bak";
    }
}
