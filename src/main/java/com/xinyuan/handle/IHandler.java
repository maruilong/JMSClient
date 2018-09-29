package com.xinyuan.handle;


import com.xinyuan.entity.DownloadInfo;
import org.springframework.stereotype.Component;

/**
 * 处理下载得到的信息
 *
 * @author shxy
 */
@Component
public interface IHandler {

    /**
     * 处理下载载信息
     *
     * @param downloadInfo 下载得到的信息对象
     * @return String 该脚本包含的sql文件的路径
     * @throws Exception
     */
    String handle(DownloadInfo downloadInfo) throws Exception;
}
