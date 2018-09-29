package com.xinyuan.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 工具类：文件名排序
 * @author Administrator
 *
 */
public class Tool {

	/**
	 * 对给定的文件名进行排序
	 * @param info 排序前的信息
	 * @return 排序前的信息
	 */
	public static BigDecimal[] order(String[]info){
		BigDecimal[]temp=new BigDecimal[0];
		if(Verify.isNull1DArray(info)){
			return temp;
		}
		int count=0;
		for(String str:info){
			if(str.matches("\\d+")){
				count++;
			}
		}
		temp=new BigDecimal[count];
		count=0;
		for(String str:info){
			if(str.matches("\\d+")){
				temp[count++]=new BigDecimal(str);
			}
		}
		BigDecimal bd=new BigDecimal("0");
		for(int i=0;i<count-1;i++){
			for(int j=i+1;j<count;j++){
				int t=temp[i].compareTo(temp[j]);
				if(t==1){
					bd=temp[i];
					temp[i]=temp[j];
					temp[j]=bd;
				}
			}
		}
		return temp;
	}
	/**
	 * 获取当前时间
	 * @return 当前系统时间
	 */
	public static String getTime(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
		return sdf.format(new Date());
	}
	/**
	 * 获取当前时间
	 * @return 当前系统时间
	 */
	public static String getDate(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}
}
