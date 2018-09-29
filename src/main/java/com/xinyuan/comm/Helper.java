package com.xinyuan.comm;

import com.xinyuan.exception.FileOperationException;
import com.xinyuan.util.FileCopy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 定义下载或上传常用的方法
 *
 * @author shxy
 */
public class Helper {

    /**
     * 获得当前时间
     *
     * @return 当前时间
     */
    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    /**
     * 删除指定文件
     *
     * @param list 文件路径
     */
    public void clear(List<String> list) {
        if (list == null) {
            return;
        }
        File file;
        for (String str : list) {
            file = new File(str);
            if (file.exists()) {
                deleteFile(file);
            }
        }
    }

    /**
     * 将文件转移到某个文件夹
     *
     * @param list      要转移的文件
     * @param targetDir 目标地址
     */
    public void move(List<String> list, String targetDir) {
        if (list == null || targetDir == null || targetDir.trim().length() == 0) {
            return;
        }
        File file;
        try {
            for (String str : list) {
                file = new File(str);
                if (!file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    FileCopy.copyDir(targetDir, str);
                } else {
                    FileCopy.copyFileToDir(targetDir, str);
                }
            }
        } catch (FileOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件或文件夹下所有文件
     *
     * @param file 要删除的文件
     */
    private void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
            file.delete();
        } else {
            file.delete();
        }
    }
}