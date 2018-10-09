package com.xinyuan.sendout;

import com.xinyuan.config.ClientConfig;
import com.xinyuan.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * 判断是否符合发送消息的条件
 *
 * @author Vic
 */
@Component
public class SendCondition {

    @Autowired
    private ClientConfig clientConfig;

    @Value("${client.time-slice}")
    private String timeSilce;

    /**
     * 判断是否符合发送条件
     *
     * @return boolean true：符合；false：不符合
     */
    public boolean isSend() {
        boolean flag = false;
        //判断发送时间是否满足发送的条件
        if (TimeUtil.compareTime(timeSilce)) {
            flag = true;
        }
        System.out.println("检测结果 " + flag);

        return flag;
    }
}
