package com.xinyuan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VPN控制类
 *
 * @author Administrator
 */
public class VPNControl {
    /**
     * 执行shell固定参数
     */
    private static final String CMD_0 = "/bin/sh";
    /**
     * 执行shell固定参数
     */
    private static final String CMD_1 = "-c";
    /**
     * 启动vpn
     */
    private static final String SHELL_VPN_START = "ipsec auto --up MY_CONN";
    /**
     * 停止vpn
     */
    private static final String SHELL_VPN_STOP = "ipsec auto --down MY_CONN";
    /**
     * 查看vpn状态
     */
    private static final String SHELL_VPN_STATUS = "ipsec auto --status";
    /**
     * 启动vpn成功时的消息码
     */
    private static final String VPN_STATUS_SUCCESS = "IPsec SA established";

    /**
     * 启动vpn
     *
     * @return true成功 false失败
     */
    public static boolean startVPN() {
        if (isConnected()) {
            return true;
        } else {
            exec(SHELL_VPN_START);
            return isConnected();
        }
    }

    /**
     * 停止vpn
     *
     * @return true成功 false失败
     */
    public static boolean stopVPN() {
        if (!isConnected()) {
            return true;
        } else {
            exec(SHELL_VPN_STOP);
            return !isConnected();
        }
    }

    /**
     * 判断vpn是否处于连接状态
     *
     * @return true连接状态 false未连接状态
     */
    private static boolean isConnected() {
        boolean result = false;
        Pattern p1 = Pattern.compile(VPN_STATUS_SUCCESS);
        Matcher m1 = p1.matcher(exec(SHELL_VPN_STATUS));
        if (m1.find()) {
            result = true;
        }
        return result;
    }

    /**
     * 执行脚本
     *
     * @param shell shell命令
     * @return 执行结果
     */
    private static String exec(String shell) {
        String[] cmd = new String[3];
        cmd[0] = CMD_0;
        cmd[1] = CMD_1;
        String back = "";
        if (shell != null) {
            cmd[2] = shell;
        }
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
            back = com.xy.util.Command.loadStream(ps.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return back;
    }

}
