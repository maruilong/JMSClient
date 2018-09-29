package com.xinyuan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
/**
 * 操作系统相关的一些操作
 * @author shxy
 *
 */
public class Sys {

	private String path;

	public Sys() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		path=loader.getResource("").getFile().substring(1);
	}
	/**
	 * 读取操作系统类型
	 * @return 操作系统类型
	 */
	public String getOs(){
		return System.getProperty("os.name").toUpperCase();
	}
	/**
	 * 读取系统环境变量
	 * @return Properties key值为变量名 value为变量值
	 * @throws IOException
	 */
	public Properties getEnv() throws IOException{
		Properties prop = new Properties();
		String OS = this.getOs();
		Process p = null;
		if (OS.indexOf("WINDOWS") !=-1) {
			p = Runtime.getRuntime().exec("cmd /c set");
		}else{
			p = Runtime.getRuntime().exec("/env");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			int i = line.indexOf("=");
			if (i > -1) {
				String key = line.substring(0, i);
				String value = line.substring(i + 1);
				prop.setProperty(key, value);
			}
		}
		return prop;
	}
	/**
	 * 读取给定的环节变量
	 * @param varName 变量名
	 * @return 变量值，如果未找到，返回null
	 * @throws IOException
	 */
	public String getEnv(String varName) throws IOException{
		if(Verify.isNullObject(varName)){
			return null;
		}
		return this.getEnv().getProperty(varName);
	}
	/**
	 * 关闭数据库
	 * @return 返回命令行输出信息
	 * @throws IOException
	 *
	 */
	public String stopDB() throws IOException{
		String OS = this.getOs();
		if (OS.indexOf("WINDOWS") !=-1) {
			return this.stopDB_win32();
		}else{
			return this.stopDB_unix();
		}
	}
	/**
	 * 启动数据库
	 * @return 返回命令行输出信息
	 * @throws IOException
	 *
	 */
	public String startDB() throws IOException{
		String OS = this.getOs();
		if (OS.indexOf("WINDOWS") !=-1) {
			return this.startDB_win32();
		}else{
			return this.startDB_unix();
		}
	}
	/**
	 * 关闭数据库,windows操作系统
	 * @return 返回命令行输出信息
	 * @throws IOException
	 *
	 */
	private String stopDB_win32() throws IOException{
		return this.execute("cmd /c "+path+"stopDB.bat");
	}
	/**
	 * 启动数据库,windows操作系统
	 * @return 返回命令行输出信息
	 * @throws IOException
	 *
	 */
	private String startDB_win32() throws IOException{
		return this.execute("cmd /c "+path+"startDB.bat");
	}
	/**
	 * 关闭数据库,unix,linux操作系统
	 * @return 返回命令行输出信息
	 * @throws IOException
	 *
	 */
	private String stopDB_unix() throws IOException{
		return this.execute("");
	}
	/**
	 * 启动数据库,unix,linux操作系统
	 * @return 返回命令行输出信息
	 * @throws IOException
	 *
	 */
	private String startDB_unix() throws IOException{
		return this.execute("");
	}
	/**
	 * 执行脚本
	 * @param cmd 执行语句
	 * @return 命令行输出信息
	 * @throws IOException
	 */
	private String execute(String cmd) throws IOException{
		String result="";
		int temp=0;
		Process p = Runtime.getRuntime().exec(cmd);
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = br.readLine()) != null) {
			if(temp==0){
				result+=line;
				temp=1;
			}else{
				result+="\n"+line;
			}
		}
		return result;
	}
}
