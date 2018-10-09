package com.xinyuan.service;

import com.xinyuan.bakrev.DownloadInfoBak;
import com.xinyuan.bakrev.UploadInfoBak;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.entity.UploadInfo;
import com.xinyuan.repository.DownLoadRepository;
import com.xinyuan.service.util.ParamCondition;
import com.xinyuan.service.util.SelectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DownloadService extends BaseService<DownLoadRepository, DownloadInfo, Long> {

    @Autowired
    private DownloadInfoBak downloadInfoBak;

    /**
     * 修改下载端的状态
     *
     * @param msgId
     * @param companyId
     * @param state
     */
    public void updateState(Long msgId, String companyId, Integer state) {

        log.info(msgId + "=======" + companyId);

        List<SelectParam> selectParams = new ArrayList<>();
        selectParams.add(new SelectParam("fileId", msgId, ParamCondition.EQUAL));
        selectParams.add(new SelectParam("companyId", companyId, ParamCondition.EQUAL));

        DownloadInfo downloadInfo = getByCondition(selectParams);
        downloadInfo.setExecuteTime(new Date());
        downloadInfo.setState(state);

        try {
            update(downloadInfo);
        } catch (Exception e) {
            try {
                downloadInfoBak.bak(downloadInfo);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


}
