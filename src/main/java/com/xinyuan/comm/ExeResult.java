package com.xinyuan.comm;

import lombok.Data;

/**
 * @author liang
 */
@Data
public class ExeResult {

    private Long msgId;

    /**
     * 0 无执行
     * 1 成功
     * 2 无响应
     * 3 失败
     */
    private Integer result;

    private String companyId;

}
