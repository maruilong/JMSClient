package com.xinyuan.sendout;

import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.Configuration;
import com.xinyuan.util.HashFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送预处理模块
 *
 * @author Vic
 */
@Component
public class SendHelper {

    /**
     * 切割成分文件的大小
     */
    public static int blockSize = 200 * 1024;
    private Logger log4j = Logger.getLogger(SendHelper.class);

    @Autowired
    private ClientConfig clientConfig;

    /**
     * 预处理文件消息(切割文件)
     *
     * @param receive         接受端
     * @param filePath        文件路径
     * @param taskId          文件id
     * @param splitFolderName 拆分文件夹名称
     *                        return String[] 分割后的文件信息
     */
    public String[] handleFile(String receive, String filePath, Long taskId, String splitFolderName) {
        String MD5 = HashFile.checkFile(filePath);

        //初始化分割文件类
        FileSplit separator = new FileSplit();
        separator.setFileName(filePath);
        separator.setSplitPath(clientConfig.getWorkDirUpload() + Configuration.spliteFileDir + splitFolderName);
        separator.setBlockSize(blockSize);
        separator.setMD5(MD5);

        //获取分文件的数量
        int blockNum = (int) separator.getBlockNum();
        //分割文件
        separator.splitFile();
        //获取分文件所有的文件名称
        String[] fileList = separator.splitNames;
        //切割失败
        if (fileList.length != blockNum) {
            log4j.error(separator);
            return null;
        }
        return fileList;
    }
}