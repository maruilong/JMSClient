package com.xinyuan.handle;

import com.xinyuan.comm.ExeResult;
import com.xinyuan.comm.Helper;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.config.Constant;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.entity.UploadType;
import com.xinyuan.exception.FileOperationException;
import com.xinyuan.service.UploadTypeService;
import com.xinyuan.util.Verify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 执行脚本辅助操作类
 *
 * @author shxy
 */
@Slf4j
@Component
public class HandleHelper extends Helper {

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private UploadTypeService uploadTypeService;

    /**
     * 脚本文件集合
     */
    private List<String> tempScript = new ArrayList<>();
    /**
     * sql文件集合
     */
    private List<String> tempSql = new ArrayList<>();


    /**
     * 执行脚本
     *
     * @param downloadInfo 要处理的文件EXECBean
     * @return boolean 是否执行成功
     * @throws Exception
     */
    public ExeResult executeSQL(DownloadInfo downloadInfo) throws Exception {
        ExeResult returnValue = new ExeResult();
        returnValue.setMsgId(downloadInfo.getFileId());
        returnValue.setCompanyId(downloadInfo.getCompanyId());
        UploadType uploadType = uploadTypeService.get(downloadInfo.getType().longValue());

        IHandler handler = Factory.getInstance(uploadType.getClassName());
        List<String> filePathList;
        try {
            filePathList = handler.handle(downloadInfo);
        } catch (Exception e) {
            throw e;
        }
        //没有要执行的脚本

        for (String filePath : filePathList) {

            if (Verify.isNullObject(filePath)) {
                returnValue.setResult(0);
            } else { //有需要执行的sql脚本
                String scriptName = this.createScript(filePath);
                String res = com.xy.util.Command.exec("sh " + scriptName);
                this.tempScript.add(scriptName);
                this.tempSql.add(filePath);
                if (res.indexOf("success") != -1 || res.indexOf("SUCCESS") != -1) {
                    returnValue.setResult(1);
                } else if (Verify.isNullObject(res)) {
                    returnValue.setResult(2);
                } else {
                    returnValue.setResult(3);
                }
            }
        }
        return returnValue;
    }

    /**
     * 创建脚本文件
     *
     * @param filePath sql文件的全路径
     * @return String sh文件的全路径
     * @throws FileOperationException
     */
    public String createScript(String filePath) throws FileOperationException {
        String scriptName = "";
        if (!filePath.endsWith(".sql")) {
            return scriptName;
        }
        File sqlFile = new File(filePath);
        if (!sqlFile.exists()) {
            return scriptName;
        }
        String dir = sqlFile.getParent();
        String sqlName = sqlFile.getName();
        scriptName = sqlName.substring(0, sqlName.length() - 4) + ".sh";
        File scriptFile = new File(getPath(dir, scriptName));
        PrintWriter pw = null;
        try {
            scriptFile.createNewFile();
            pw = new PrintWriter(new FileOutputStream(scriptFile));
            pw.println(Constant.SCRIPT_1);
            pw.println(Constant.SCRIPT_3);
            pw.println(Constant.SCRIPT_5);
            pw.println(Constant.SCRIPT_6 + filePath);
            pw.flush();
        } catch (IOException e) {
            throw new FileOperationException("创建脚本文件失败", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return scriptFile.getAbsolutePath();
    }

    /**
     * 获取文件全路径
     *
     * @param dir      目录名
     * @param fileName 文件名
     * @return String 文件全路径
     */
    private String getPath(String dir, String fileName) {
        if (dir.endsWith(File.separator)) {
            return dir + fileName;
        } else {
            return dir + File.separator + fileName;
        }
    }

    /**
     * 清除或转移已处理过的文件
     */
    public void clear() {
        this.move(tempSql, clientConfig.getHistoryDirDownload());
        this.clear(tempScript);
        this.clear(tempSql);
    }
}