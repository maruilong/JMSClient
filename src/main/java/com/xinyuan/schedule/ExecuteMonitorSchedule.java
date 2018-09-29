package com.xinyuan.schedule;

import com.xinyuan.comm.ExeResult;
import com.xinyuan.comm.MessageBak;
import com.xinyuan.comm.enums.DownloadState;
import com.xinyuan.comm.enums.MessageType;
import com.xinyuan.comm.enums.UploadState;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.message.MessageInfo;
import com.xinyuan.sender.MessageSender;
import com.xinyuan.sender.SendInfo;
import com.xinyuan.service.DownloadService;
import com.xinyuan.service.ExecuteService;
import com.xinyuan.service.util.ParamCondition;
import com.xinyuan.service.util.SelectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExecuteMonitorSchedule {

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private SendInfo sendInfo;

    /**
     * 发送等待时间
     */
    @Scheduled(fixedRate = 50000)
    public void checkTask() throws Exception {

        //检测是否有信息要处理
        List<SelectParam> selectParams = new ArrayList<>();
        selectParams.add(new SelectParam("state", DownloadState.DOWNLOADED.getState(), ParamCondition.EQUAL));

        List<DownloadInfo> downloadInfoList = downloadService.findByCondition(selectParams);

        if (!CollectionUtils.isEmpty(downloadInfoList)) {
            List<ExeResult> exeResultList = executeService.handleFile(downloadInfoList);

            for (ExeResult exeResult : exeResultList) {
                if (exeResult.getResult() != 2) {

                    MessageBak messageBak = new MessageBak();
                    messageBak.setMsgType(MessageType.EXECUTED.getType());
                    messageBak.setMsgId(exeResult.getMsgId());
                    messageBak.setReceiver(exeResult.getCompanyId());
                    messageBak.setRecOrExec("1");
                    sendInfo.sendMsgInfo(messageBak);

                    downloadService.updateState(messageBak.getMsgId(), messageBak.getReceiver(), UploadState.EXECUTED.getState());
                }
            }
        }
    }
}
