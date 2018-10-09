package com.xinyuan.service;

import com.xinyuan.comm.ExeResult;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.handle.HandleHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liang
 */
@Slf4j
@Service
public class ExecuteService {

    @Autowired
    private HandleHelper handleHelper;

    public List<ExeResult> handleFile(List<DownloadInfo> downloadInfos) {
        List<ExeResult> returnValue = new ArrayList<>();

        Long lastFileId = 0L;

        //1 -1
        //1001 1
        //1002 1001

        try {
            for (DownloadInfo downloadInfo : downloadInfos) {
                if (lastFileId.equals(downloadInfo.getFileId())) {
                    continue;
                } else {
                    lastFileId = downloadInfo.getFileId();
                }
                //执行脚本
                returnValue.add(handleHelper.executeSQL(downloadInfo));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            handleHelper.clear();
        }
        return returnValue;
    }
}
