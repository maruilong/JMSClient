package com.xinyuan.schedule;

import com.xinyuan.bakrev.Recover;
import com.xinyuan.comm.enums.UploadState;
import com.xinyuan.entity.UploadInfo;
import com.xinyuan.sendout.SendCondition;
import com.xinyuan.service.SendService;
import com.xinyuan.service.UploadService;
import com.xinyuan.service.util.ParamCondition;
import com.xinyuan.service.util.SelectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liang
 * 定时发送文件线程
 */
@Slf4j
@Component
public class SendMessageSchedule {

    @Autowired
    private SendCondition sendCondition;

    @Autowired
    private SendService sendService;

    @Autowired
    private UploadService uploadService;

    /**
     * 发送等待时间
     */
    @Scheduled(fixedRate = 50000)
    public void checkTask() throws Exception {
        new Recover().recSaveInfo();

        //如果满足发送条件
        if (sendCondition.isSend()) {
            //读取备份内容 发送消息
            sendService.initSend();

            //查询新消息 并且发送
            List<SelectParam> selectParams = new ArrayList<>();
            selectParams.add(new SelectParam("state", UploadState.UNSENT.getState(), ParamCondition.EQUAL));
            List<UploadInfo> uploadInfoList = uploadService.findByCondition(selectParams);

            for (UploadInfo uploadInfo : uploadInfoList) {
                sendService.send(uploadInfo);
            }
        }
    }

}
