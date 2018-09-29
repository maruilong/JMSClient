package com.xinyuan.sendout;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

/**
 * 分割文件所组成的类，分割成固定大小的文件
 *
 * @author zeng
 */
public class FileSplit {

    private Logger log4j = Logger.getLogger(FileSplit.class);

    /**
     * 待切割文件的全路径
     */
    private String singleFileAndPath;
    /**
     * 切割成文件的大小
     */
    private long blockSize;
    /**
     * 要将分文件保存的目录
     */
    private String splitFilePath;
    /**
     * MD5码
     */
    private String MD5 = null;

    /**
     * 所有分文件名所组成
     */
    public String[] splitNames = null;

    /**
     * 设置待切割文件的全路径
     *
     * @param fileName 文件名称
     */
    public void setFileName(String fileName) {
        this.singleFileAndPath = fileName;
    }

    /**
     * 设置分文件的大小
     *
     * @param blockSize 分文件的大小
     */
    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * 设置分文件保存的目录
     *
     * @param splitFilePath 分文件保存目录
     */
    public void setSplitPath(String splitFilePath) {
        this.splitFilePath = splitFilePath;
    }

    /**
     * 设置MD5码
     *
     * @param md5 MD5码
     */
    public void setMD5(String md5) {
        this.MD5 = md5;
    }

    /**
     * 获取分文件的数量
     *
     * @return long 分文件的数量
     */
    public long getBlockNum() {
        File file = new File(singleFileAndPath);
        long fileSize = file.length();
        if (fileSize <= blockSize) {
            // 当待切割文件的大小小于切割成分文件的大小，文件不分割
            return 1;
        } else {
            if (fileSize % blockSize > 0) {
                return fileSize / blockSize + 1;
            } else {
                return fileSize / blockSize;
            }
        }
    }

    /**
     * 产生各个分文件的文件路径
     *
     * @param currentBlock 当前分文件
     * @return String 分文件的路径
     */
    private String generateSeparatorFileName(long currentBlock) {
        long blockNum = getBlockNum();
        int s = singleFileAndPath.lastIndexOf(File.separator);
        String filename = singleFileAndPath.substring(s);
        return splitFilePath + filename + "_" + MD5 + "_" + blockNum + "_" + currentBlock;
    }

    /**
     * 将信息写入分文件
     *
     * @param fileSeparateName 分文件的名称
     * @param beginPos         起始位置
     * @return boolean 是否写入成功
     */
    private boolean writeFile(String fileSeparateName, long beginPos) {

        RandomAccessFile raf = null;
        FileOutputStream fos = null;
        byte[] bt = new byte[1024];
        long writeByte = 0;
        int len = 0;
        try {
            //用可随机读取位置的方式读取文件
            raf = new RandomAccessFile(singleFileAndPath, "r");
            raf.seek(beginPos);
            fos = new FileOutputStream(fileSeparateName);
            while ((len = raf.read(bt)) > 0) {
                //当writeByte大于blockSize时才停止从文件中读取
                if (writeByte < blockSize) {
                    writeByte = writeByte + len;
                    if (writeByte <= blockSize)
                        fos.write(bt, 0, len);
                    else {
                        len = len - (int) (writeByte - blockSize);
                        fos.write(bt, 0, len);
                    }
                }
            }
            fos.close();
            raf.close();
        } catch (Exception e) {
            //IO异常
            e.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
                if (raf != null) {
                    raf.close();
                }
            } catch (Exception f) {
                String errorinfo = e.toString() + "\n" + "fileName:" + e.getStackTrace()[0].getFileName()
                        + "\n" + "fileLine:" + e.getStackTrace()[0].getLineNumber();
                log4j.error(errorinfo);
                f.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * 分割文件
     *
     * @return boolean 是否成功
     */
    private boolean separatorFile() {
        long blockNum = getBlockNum();
        splitNames = new String[(int) blockNum];
        File file = new File(singleFileAndPath);
        long fileSize = file.length();
        if (blockNum == 1)
            blockSize = fileSize;
        //要写的文件的大小
        long writeSize = 0;
        //已经写的大小
        long writeTotal = 0;
        String FileCurrentNameAndPath;
        for (int i = 1; i <= blockNum; i++) {
            if (i < blockNum) {
                writeSize = blockSize;
            } else {
                //除最后一个外，要写的文件的大小全固定是blockSize
                writeSize = fileSize - writeTotal;
            }
            if (blockNum == 1) {
                FileCurrentNameAndPath = generateSeparatorFileName(1);
                splitNames[0] = FileCurrentNameAndPath;
            } else {
                FileCurrentNameAndPath = generateSeparatorFileName(i);
                splitNames[i - 1] = FileCurrentNameAndPath;
            }
            if (!writeFile(FileCurrentNameAndPath, writeTotal))
                return false;
            //累加写的大小
            writeTotal = writeTotal + writeSize;
        }
        return true;
    }

    /**
     * 分割文件 主方法(外部调用)
     */
    public void splitFile() {
        File file = new File(singleFileAndPath);
        if (file.isDirectory()) {
            log4j.error("path is not a file~!!");
        } else {
            if (separatorFile()) {
                log4j.info("split file success~!!");
            } else {
                log4j.error("split file fail~!!");
            }
        }
    }
}