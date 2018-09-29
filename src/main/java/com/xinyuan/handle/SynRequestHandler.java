package com.xinyuan.handle;

import com.xinyuan.comm.Helper;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.Configuration;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.exception.FileOperationException;
import com.xinyuan.message.MessageInfo;
import com.xinyuan.sender.MessageSender;
import com.xinyuan.service.DownloadService;
import com.xinyuan.util.FileCopy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liang
 */
@Slf4j
@Component
public class SynRequestHandler extends Helper implements IHandler {

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private FileResolve fileResolve;

    @Autowired
    private MessageSender messageSender;

    @Override
    public String handle(DownloadInfo downloadInfo) throws Exception {
        String returnValue = null;
        //执行同步任务
        String filePath = downloadInfo.getFilePath();

        File file = new File(filePath);

        List<String> tmpFile = new ArrayList<>();
        List<String> copyFile = new ArrayList<>();
        try {

            if (file.exists()) {
                //拷贝到工作目录
                File synDir = new File(clientConfig.getWorkDirDownload() + Configuration.datasyn_request + downloadInfo.getCompanyId());
                FileCopy.createFile(synDir, false);
                //拷贝到工作目录
                FileCopy.copyFileToDir(synDir.getAbsolutePath(), file.getPath());
                copyFile.add(synDir.getAbsolutePath() + File.separator + file.getName());

                //文件执行
                List<String> execute = fileResolve.execute(synDir.getAbsolutePath() + File.separator + file.getName());

                MessageInfo messageInfo = new MessageInfo();
                messageSender.send();
                //处理文件
            } else {
                log.error("filePath not found" + filePath);
                throw new FileOperationException("filePath not found" + filePath);
            }
        } catch (Exception e) {

        } finally {
            this.move(copyFile, clientConfig.getHistoryDirDownload());
            this.clear(tmpFile);
            this.clear(copyFile);
        }

        return returnValue;
    }
}
