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
 * ����һ��sql�ű�(����֪ͨ����)
 *
 * @author shxy
 */
@Component
public class DataHandler extends Helper implements IHandler {

    /**
     * ����ִ���ļ�
     *
     * @param downloadInfo ִ��bean��
     * @return �������ļ�·��
     * @throws Exception
     */
    @Override
    public String handle(DownloadInfo downloadInfo) throws Exception {
        ClientConfig clientConfig = SpringUtil.getBean(ClientConfig.class);

        String scriptPath = null;
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
                //ֱ���ǽű��ļ�
                if (fileName.endsWith(".sql")) {
                    FileCopy.copyFileToDir(commonDir.getAbsolutePath(), filePath + fileName);
                    scriptPath = commonDir.getAbsolutePath() + File.separator + fileName;
                    //ѹ���ļ�
                } else if (fileName.endsWith(".zip")) {
                    dirPath = dir + File.separator + fileName + "_" + tmp;
                    File dirFile = new File(dirPath);
                    FileCopy.createFile(dirFile, false);
                    FileZip.unzip(filePath, dirPath);
                    File[] files = dirFile.listFiles();
                    for (File f : files) {
                        if (f.getName().endsWith(".sql")) {
                            //����������Ŀ¼
                            FileCopy.copyFileToDir(commonDir.getAbsolutePath(), f.getAbsolutePath());
                            scriptPath = commonDir.getAbsolutePath() + File.separator + f.getName();
                        } else {
                            //����������Ŀ¼
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
        return scriptPath;
    }
}