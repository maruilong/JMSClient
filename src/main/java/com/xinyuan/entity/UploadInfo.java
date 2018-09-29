package com.xinyuan.entity;

import static javax.persistence.GenerationType.IDENTITY;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author liang
 */
@Data
@Entity
@Table(name = "upload_info")
public class UploadInfo implements Serializable {

    /**
     * 编号
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 上一文件编号
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
     * 未发送,已发送,已下载,已执行
     */
    @Column(name = "state")
    private Integer state;

    /**
     * 上传时间
     */
    @Column(name = "upload_time")
    private Date uploadTime;

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
     * 批量
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 交互信息是否上传
     */
    @Column(name = "is_upload")
    private Integer isUpload;

    /**
     * 下载时间
     */
    @Column(name = "download_time")
    private Date downloadTime;

    /**
     * 执行时间
     */
    @Column(name = "execute_date")
    private Date executeDate;

    /**
     * 发送方ID
     */
    @Column(name = "company_id")
    private String companyId;

}
