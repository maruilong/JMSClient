package com.xinyuan.config;

import java.io.File;

/**
 * @author liang
 */
public class Configuration {

    /**
     * 切割文件存放的目录
     */
    public static String spliteFileDir = "splite" + File.separator;

    /**
     * 脚本文件存放目录
     */
    public static String common_script = "common_script" + File.separator;

    /**
     * 通知提醒附件目录
     */
    public static String noticefile = "noticefile" + File.separator;

    /**
     * 接收后的文件存放临时目录
     */
    public static String fileSave_temp = "save_file" + File.separator;

    /**
     * 合并后文件存放目录
     */
    public static String save_destination = "destination" + File.separator;
/**
     * 合并后文件存放目录
     */
    public static String datasyn_request = "datasyn_request" + File.separator;

}
