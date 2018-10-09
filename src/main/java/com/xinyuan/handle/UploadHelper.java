package com.xinyuan.handle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import com.xinyuan.comm.Helper;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.exception.FileOperationException;
import com.xinyuan.util.HashFile;
import com.xinyuan.util.Verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上传信息辅助操作类
 *
 * @author Vic.z
 */
@Component
public class UploadHelper extends Helper {

    @Autowired
    private ClientConfig clientConfig;

    /**
     * 产生唯一的文件名
     *
     * @param path 目标路径
     * @param ext  文件扩展名
     * @return 文件名
     */
    public String getFileName(String path, String ext) {
        String fileName = null;
        if (Verify.isNullObject(path, ext)) {
            return fileName;
        }
        String temp = this.getTime();
        Random random = new Random();
        String rand = random.nextInt(10000) + "";
        fileName = temp + rand + "." + ext;
        File file = new File(path + fileName);
        while (file.exists()) {
            rand = (1 - Math.random()) * 10000 + "";
            fileName = temp + rand + "." + ext;
            file = new File(path + fileName);
        }
        return clientConfig.getCompanyId() + "-" + fileName;
    }

    /**
     * 创建sql文件
     *
     * @param path    文件路径
     * @param content 文件内容
     * @return 文件名
     * @throws FileOperationException
     */
    public String createSqlFile(String path, List<String> content) throws FileOperationException {
        if (Verify.isNullObject(path, content)) {
            return null;
        }
        String fileName = this.getFileName(path, "sql");
        File file = new File(path + fileName);
        PrintWriter pw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            ContentResolve.writerToFile(pw, content);
            pw.flush();
        } catch (IOException e) {
            throw new FileOperationException("创建sql文件失败!", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return fileName;
    }

    /**
     * @param filePath 文件路径
     * @return 文件的md5码 DataHandler
     */
    public String getMD5(String filePath) {
        return HashFile.checkFile(filePath);
    }

}
