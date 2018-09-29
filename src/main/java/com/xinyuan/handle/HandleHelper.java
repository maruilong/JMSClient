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
 * ִ�нű�����������
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
     * �ű��ļ�����
     */
    private List<String> tempScript = new ArrayList<>();
    /**
     * sql�ļ�����
     */
    private List<String> tempSql = new ArrayList<>();


    /**
     * ִ�нű�
     *
     * @param downloadInfo Ҫ������ļ�EXECBean
     * @return boolean �Ƿ�ִ�гɹ�
     * @throws Exception
     */
    public ExeResult executeSQL(DownloadInfo downloadInfo) throws Exception {
        ExeResult returnValue = new ExeResult();
        returnValue.setMsgId(downloadInfo.getFileId());

        UploadType uploadType = uploadTypeService.get(downloadInfo.getType().longValue());

        IHandler handler = Factory.getInstance(uploadType.getClassName());
        String filePath;
        try {
            filePath = handler.handle(downloadInfo);
        } catch (Exception e) {
            throw e;
        }
        //û��Ҫִ�еĽű�
        if (Verify.isNullObject(filePath)) {
            returnValue.setResult(0);
        } else { //����Ҫִ�е�sql�ű�
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
        return returnValue;
    }

    /**
     * �����ű��ļ�
     *
     * @param filePath sql�ļ���ȫ·��
     * @return String sh�ļ���ȫ·��
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
            String osName = System.getProperties().getProperty("os.name");
            if (osName.contains("Linux")) {
                pw.println(Constant.SCRIPT_2 + clientConfig.getLocalOracleHome());
            }
            pw.println(Constant.SCRIPT_3);
            pw.println(Constant.SCRIPT_4);
            pw.println(Constant.SCRIPT_5);
            pw.println(Constant.SCRIPT_6 + filePath);
            pw.flush();
        } catch (IOException e) {
            throw new FileOperationException("�����ű��ļ�ʧ��", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return scriptFile.getAbsolutePath();
    }

    /**
     * ��ȡ�ļ�ȫ·��
     *
     * @param dir      Ŀ¼��
     * @param fileName �ļ���
     * @return String �ļ�ȫ·��
     */
    private String getPath(String dir, String fileName) {
        if (dir.endsWith(File.separator)) {
            return dir + fileName;
        } else {
            return dir + File.separator + fileName;
        }
    }

    /**
     * �����ת���Ѵ�������ļ�
     */
    public void clear() {
        this.move(tempSql, clientConfig.getHistoryDirDownload());
        this.clear(tempScript);
        this.clear(tempSql);
    }
}