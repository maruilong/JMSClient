package com.xinyuan.util;

import java.io.*;
import java.security.*;
/**
 * 生成效验码
 * @author Administrator
 *
 */
public class HashFile {
	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String checkFile(String filePath) {
		String hashType = "MD5";
		return getHash(filePath, hashType);
	}

	/**
	 * 功能：生成校验码
	 *
	 * @param fileName 文件名称
	 * @param hashType 文件类型
	 * @return 返回效验码，如果是""表示出错
	 */
	private static String getHash(String fileName, String hashType) {
		InputStream fis;
		String rtHash = "";
		try {
			fis = new FileInputStream(fileName);
			byte[] buffer = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance(hashType);
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			rtHash = toHexString(md5.digest());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return rtHash;
	}

	/**
	 * 生成十六进制的字符串
	 *
	 * @param b 需处理的信息
	 * @return 十六进制字符串
	 */
	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
}
