package com.xy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
/**
 * 执行脚本类
 * @author shxy
 *
 */
public class Command {
	/**执行shell固定参数*/
	private static final String CMD_0="/bin/sh";
	/**执行shell固定参数*/
	private static final String CMD_1="-c";
	/**启动tomcat*/
	private static final String SHELL_START_TOMCAT="startup.sh";
	/**获取tomcat pid*/
	private static final String SHELL_FIND_TOMCAT="ps -ef|grep tomcat";
	/**根据pid结束进程*/
	private static final String SHELL_KILL="kill -9 ";
	/**启动vpn*/
	private static final String SHELL_START_VPN="ipsec auto --up MY_CONN";
	/**停止vpn*/
	private static final String SHELL_STOP_VPN="ipsec auto --down MY_CONN";
	/**启动vpn成功时的消息码*/
	private static final String VPN_CODE_1="117";
	/**启动vpn成功时的消息码*/
	private static final String VPN_CODE_2="004";

	private static Logger log=Logger.getLogger(Command.class);
	/**
	 * 执行脚本
	 * @param shell shell命令
	 * @return 执行结果
	 */
	public static String exec(String shell) {
		String[] cmd = new String[3];
		cmd[0] = CMD_0;
		cmd[1] = CMD_1;
		String back = "";
		if (shell != null) {
			cmd[2] = shell;
		}
		try {
			Process ps = Runtime.getRuntime().exec(cmd);
			back = loadStream(ps.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		cmd=null;
		log.info("execute result:"+back);
		return back;
	}
	/**
	 * 启动tomcat
	 *
	 */
	public static void startTomcat() {
		exec(SHELL_START_TOMCAT);
	}
	/**
	 * 停止tomcat
	 *
	 */
	public static void stopTomcat() {
		int pid = matchInfo(exec(SHELL_FIND_TOMCAT));
		exec(SHELL_KILL + pid);
	}
	/**
	 * 启动vpn
	 *
	 */
	public static boolean startVPN() {
		boolean result = false;
		String rex1 = VPN_CODE_1;
		String rex2 = VPN_CODE_2;
		String str = exec(SHELL_START_VPN);
		Pattern p = Pattern.compile(rex1);
		Matcher m = p.matcher(str);
		if (m.find()) {
			p = Pattern.compile(rex2);
			m = p.matcher(str);
			int count = 0;
			while (m.find()) {
				count++;
			}
			if (count == 1 || count == 2) {
				result = true;
			}
		}
		return result;
	}
	/**
	 * 停止vpn
	 *
	 */
	public static void stopVPN() {
		exec(SHELL_STOP_VPN);
	}
	/**
	 * 私有方法 匹配字符串中的数字
	 * @param str 源字符串
	 * @return 字符串中的数字
	 */
	private static int matchInfo(String str) {
		int size = -1;
		String regEx = "[0-9]+";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		if (m.find()) {
			size = Integer.valueOf(m.group());
		}
		return size;
	}
	/**
	 * 获取执行脚本后的响应信息
	 * @param in 输入流
	 * @return 输入流的内容
	 * @throws IOException
	 */
	public static String loadStream(InputStream in) throws IOException {
		String put = "";
		String back = "";
		InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
		BufferedReader br = new BufferedReader(inReader);
		while ((put = br.readLine()) != null) {
			back += put + "\n";
		}
		return back;
	}
//	public static void main(String[] args) {
//		try {
//			Process p = Runtime.getRuntime().exec("sh /usr/tmp/00124678_20100519162153_301_begin.sh");
//			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String line;
//			while ((line = br.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
