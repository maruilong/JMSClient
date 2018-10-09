package com.xinyuan.handle;

import com.xinyuan.comm.Helper;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.Configuration;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.exception.FileOperationException;
import com.xinyuan.util.FileCopy;
import com.xinyuan.util.FileZip;
import com.xinyuan.util.SpringUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理一般sql脚本(包括通知附件)
 *
 * @author shxy
 */
@Component
public class DataHandler extends Helper implements IHandler {

    /**
     * 处理执行文件
     *
     * @param downloadInfo 执行bean类
     * @return 处理后的文件路径
     * @throws Exception
     */
    @Override
    public List<String> handle(DownloadInfo downloadInfo) throws Exception {
        ClientConfig clientConfig = SpringUtil.getBean(ClientConfig.class);

        List<String> scriptPathList = new ArrayList<>();

        String filePath = downloadInfo.getFilePath();
        File file = new File(filePath);
        String dir;
        String fileName;
        String tmp = this.getTime() + "_tmp";
        List<String> tmpFile = new ArrayList<>();
        String dirPath;
        try {
            if (file.exists()) {
                dir = file.getParent();
                fileName = downloadInfo.getFileName();
                File commonDir = new File(clientConfig.getWorkDirDownload() + Configuration.common_script + File.separator + downloadInfo.getCompanyId());
                FileCopy.createFile(commonDir, false);
                //直接是脚本文件
                if (fileName.endsWith(".sql")) {
                    System.out.println(commonDir.getAbsolutePath());
                    FileCopy.copyFileToDir(commonDir.getAbsolutePath(), filePath + fileName);
                    scriptPathList.add(commonDir.getAbsolutePath() + File.separator + fileName);
                    //压缩文件
                } else if (fileName.endsWith(".zip")) {
                    dirPath = dir + File.separator + fileName + "_" + tmp;
                    File dirFile = new File(dirPath);
                    FileCopy.createFile(dirFile, false);
                    FileZip.unzip(filePath+fileName, dirPath);
                    File[] files = dirFile.listFiles();
                    for (File f : files) {
                        if (f.getName().endsWith(".sql")) {
                            //拷贝到工作目录
                            FileCopy.copyFileToDir(commonDir.getAbsolutePath(), f.getAbsolutePath());
                            scriptPathList.add(commonDir.getAbsolutePath() + File.separator + f.getName());
                        } else {
                            //拷贝到附件目录
                            FileCopy.copyFileToDir(clientConfig.getAttachDirDownload(), f.getAbsolutePath());
                        }
                    }
                    tmpFile.add(dirPath);
                }
            } else {
                throw new FileOperationException("could not find the fileName:" + filePath);
            }
        } catch (FileOperationException e) {
            throw e;
        } finally {
            this.clear(tmpFile);
        }
        return scriptPathList;
    }
}