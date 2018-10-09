package com.xinyuan.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 处理与时间相关的类
 *
 * @author shxy
 */
public class TimeUtil {
    /**
     * 获取当前时间
     *
     * @return 返回"yyyy-MM-dd HH:mm:ss"的字符串
     */
    public static String getNowTime() {
        Date now = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = dateformat.format(now);
        return nowTime;
    }

    /**
     * 比较数据类型为(**-**)的函数，看当前时间是否在所在范围内
     *
     * @param timeSlice
     * @return 是否在所在范围
     */
    public static boolean compareTime(String timeSlice) {
        //timeSlice
        int starthour = Integer.parseInt(timeSlice.substring(0, 2));
        //timeSlice
        int endhour = Integer.parseInt(timeSlice.substring(3, 5));
        String current_time = getNowTime();
        //timeSlice
        int current_hour = Integer.parseInt(current_time.substring(11, 13));

        System.out.println(starthour + "----" + endhour + "-----" + current_hour);
        //为跨天时间
        if (starthour > endhour) {
            if ((current_hour >= starthour) || (current_hour < endhour)) {
                return true;
            } else {
                return false;

            }
        } else {//在同一天内
            if ((current_hour >= starthour) && (current_hour < endhour)) {
                return true;
            } else {
                return false;
            }
        }
    }
}