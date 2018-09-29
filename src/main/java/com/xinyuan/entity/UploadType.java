package com.xinyuan.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author liang
 */
@Data
@Entity
@Table(name = "upload_type")
public class UploadType {

    /**
     * 编号
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 类型名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 是否同步
     */
    @Column(name = "is_synchronous")
    private Integer isSynchronous;
    /**
     * 是否批量
     */
    @Column(name = "batch")
    private Integer batch;
    /**
     * 状态
     * 未发送,已发送,已下载,已执行
     */
    @Column(name = "class_name")
    private String className;

}
