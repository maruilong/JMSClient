package com.xinyuan.util;

import com.xinyuan.exception.FileOperationException;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件操作类
 *
 * @author shxy
 */
public class FileCopy {

    private static Logger logger = Logger.getLogger(FileCopy.class);

    /**
     * 复制目录下的文件（不包括该目录）到指定目录，会连同子目录一起复制过去。
     *
     * @param targetDir 目标路径
     * @param path      源路径
     * @throws FileOperationException
     */
    public static void copyFileFromDir(String targetDir, String path) throws FileOperationException {
        File file = new File(path);
        createFile(targetDir, false);
        if (file.isDirectory()) {
            copyFileToDir(targetDir, listFile(file));
        }
    }

    /**
     * 复制目录下的文件（不包含该目录和子目录，只复制目录下的文件）到指定目录。
     *
     * @param targetDir 目标路径
     * @param path      源路径
     * @throws FileOperationException
     */
    public static void copyFileOnly(String targetDir, String path) throws FileOperationException {
        File file = new File(path);
        File targetFile = new File(targetDir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                if (subFile.isFile()) {
                    copyFile(targetFile, subFile);
                }
            }
        }
    }

    /**
     * 复制目录到指定目录，该方法会将path以及path下的文件和子目录全部复制到目标目录
     *
     * @param targetDir 目标目录
     * @param path      源路径
     * @throws FileOperationException
     */
    public static void copyDir(String targetDir, String path) throws FileOperationException {
        File targetFile = new File(targetDir);
        createFile(targetFile, false);
        File file = new File(path);
        if (targetFile.isDirectory() && file.isDirectory()) {
            copyFileToDir(targetFile.getAbsolutePath() + "/" + file.getName(),
                    listFile(file));
        }
    }

    /**
     * 复制目录到指定目录，只复制目录
     *
     * @param targetDir 目标目录
     * @param path      源目录
     * @throws FileOperationException
     */
    public static void copyDirOnly(String targetDir, String path) throws FileOperationException {
        File targetFile = new File(targetDir);
        createFile(targetFile, false);
    }

    /**
     * 复制一组文件到指定目录
     *
     * @param targetDir 目标目录
     * @param filePath  需要复制的文件路径
     * @throws FileOperationException
     */
    public static void copyFileToDir(String targetDir, String... filePath) throws FileOperationException {
        if (targetDir == null || "".equals(targetDir)) {
            logger.debug("参数错误,目标路径不能为空!");
            return;
        }
        File targetFile = new File(targetDir);
        if (!targetFile.exists()) {
            targetFile.mkdir();
        } else {
            if (!targetFile.isDirectory()) {
                logger.debug("参数错误,目标路径指向的不是一个目录!");
                return;
            }
        }
        for (String path : filePath) {
            File file = new File(path);
            System.out.println("file:" + file);
            System.out.println("targetDir" + targetDir);
            if (file.isDirectory()) {
                copyFileToDir(targetDir + File.separator + file.getName(), listFile(file));
            } else {
                copyFileToDir(targetDir, file, "");
            }
        }
    }

    /**
     * 复制文件到指定目录
     *
     * @param targetDir 目标目录
     * @param file      源文件
     * @param newName   重命名的名字
     * @throws FileOperationException
     */
    public static void copyFileToDir(String targetDir, File file, String newName) throws FileOperationException {
        String newFile = "";
        if (newName != null && !"".equals(newName)) {
            newFile = targetDir + File.separator + newName;
        } else {
            newFile = targetDir + File.separator + file.getName();
        }
        File tFile = new File(newFile);
        copyFile(tFile, file);
    }

    /**
     * 复制文件
     *
     * @param targetFile 目标文件
     * @param file       源文件
     * @throws FileOperationException
     */
    public static void copyFile(File targetFile, File file) throws FileOperationException {
        createFile(targetFile, true);
        BufferedInputStream is = null;
        BufferedOutputStream fos = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            fos = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] buffer = new byte[2048];
            int ch = -1;
            while ((ch = is.read(buffer)) != -1) {
                fos.write(buffer, 0, ch);
                buffer = new byte[2048];
            }
            fos.flush();
        } catch (Exception e) {
            throw new FileOperationException("创建文件异常!", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取目录下的文件列表
     *
     * @param dir 目标文件
     * @return String[] 文件列表
     */
    public static String[] listFile(File dir) {
        String absolutPath = dir.getAbsolutePath();
        String[] paths = dir.list();
        String[] files = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = absolutPath + File.separator + paths[i];
        }
        return files;
    }

    /**
     * 创建文件或目录
     *
     * @param path   文件路径
     * @param isFile 是文件或目录
     */
    public static void createFile(String path, boolean isFile) {
        createFile(new File(path), isFile);
    }

    /**
     * 创建文件或目录
     *
     * @param file   目标文件
     * @param isFile 是文件或目录
     */
    public static void createFile(File file, boolean isFile) {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (isFile) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                file.mkdir();
            }
        }
    }
}