package com.xinyuan.service;

import com.xinyuan.bakrev.UploadInfoBak;
import com.xinyuan.entity.UploadInfo;
import com.xinyuan.repository.UploadRepository;
import com.xinyuan.service.util.ParamCondition;
import com.xinyuan.service.util.SelectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liang
 */
@Slf4j
@Service
public class UploadService extends BaseService<UploadRepository, UploadInfo, Long> {

    @Autowired
    private UploadInfoBak uploadInfoBak;

    /**
     * 修改上传端的状态
     *
     * @param msgId
     * @param companyId
     * @param state
     */
    public void updateState(Long msgId, String companyId, Integer state) {

        List<SelectParam> selectParams = new ArrayList<>();
        selectParams.add(new SelectParam("id", msgId, ParamCondition.EQUAL));
        selectParams.add(new SelectParam("companyId", companyId, ParamCondition.EQUAL));

        UploadInfo uploadInfo = getByCondition(selectParams);

        if (uploadInfo == null) {
            log.error(msgId + "-----------------" + companyId);
        }
        uploadInfo.setIsUpload(1);
        uploadInfo.setUploadTime(new Date());
        uploadInfo.setState(state);

        try {
            update(uploadInfo);
        } catch (Exception e) {
            try {
                uploadInfoBak.bak(uploadInfo);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
