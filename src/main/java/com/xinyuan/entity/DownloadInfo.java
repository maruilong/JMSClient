package com.xinyuan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author liang
 */
@Data
@Entity
@Table(name = "download_info")
public class DownloadInfo {

    /**
     * 编号
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 上一个文件编号
     */
    @Column(name = "last_id")
    private Long lastId;

    /**
     * 文件名称
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @Column(name = "file_path")
    private String filePath;

    /**
     * 状态
     * 已下载,已执行,执行失败
     */
    @Column(name = "state")
    private Integer state;

    /**
     * 下载时间
     */
    @Column(name = "download_time")
    private Date downloadTime;

    /**
     * 执行时间
     */
    @Column(name = "execute_time")
    private Date executeTime;

    /**
     * 发送方编号
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 是否同步
     */
    @Column(name = "is_synchronous")
    private Integer isSynchronous;

    /**
     * 批量
     */
    @Column(name = "batch")
    private Integer batch;

    /**
     * 发送方文件ID
     */
    @Column(name = "file_id")
    private Long fileId;

    /**
     * 类型
     */
    @Column(name = "type")
    private Integer type;
}
