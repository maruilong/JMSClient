package com.xinyuan.service;

import com.xinyuan.bakrev.UploadInfoBak;
import com.xinyuan.comm.InitSend;
import com.xinyuan.comm.SendBean;
import com.xinyuan.comm.enums.UploadState;
import com.xinyuan.entity.UploadInfo;
import com.xinyuan.sender.SendInfo;
import com.xinyuan.service.util.ParamCondition;
import com.xinyuan.service.util.SelectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liang
 */
@Slf4j
@Service
public class SendService {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UploadInfoBak uploadInfoBak;

    @Autowired
    private SendInfo sendInfo;


    /**
     *
     */
    public void initSend() {
        List<InitSend> initSends = this.sendInfo.sendInit();

        if (!CollectionUtils.isEmpty(initSends)) {
            for (InitSend initSend : initSends) {
                UploadInfo uploadInfo = null;
                try {
                    //修改文件类型为三 的状态改完发送成功
                    List<SelectParam> selectParams = new ArrayList<>();
                    selectParams.add(new SelectParam("id", initSend.getTaskId(), ParamCondition.EQUAL));
                    selectParams.add(new SelectParam("companyId", initSend.getCompanyId(), ParamCondition.EQUAL));
                    uploadInfo = uploadService.getByCondition(selectParams);
                    uploadInfo.setState(UploadState.SENT.getState());
                    uploadInfo.setCompanyId(initSend.getCompanyId());

                    uploadService.update(uploadInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        uploadInfoBak.bak(uploadInfo);
                    } catch (IOException e1) {
                        log.error(e1.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 将文件发送到客户端
     *
     * @param uploadInfo
     */
    public void send(UploadInfo uploadInfo) throws Exception {
        SendBean sendBean = new SendBean();
        sendBean.setReceiver(uploadInfo.getCompanyId());
        sendBean.setFileName(uploadInfo.getFileName());
        sendBean.setFilePath(uploadInfo.getFilePath());
        sendBean.setTaskId(uploadInfo.getId());
        sendBean.setFileType(uploadInfo.getType());
        sendBean.setLastFileId(uploadInfo.getLastId());
        sendInfo.sendFileInfo(sendBean);
    }
}
