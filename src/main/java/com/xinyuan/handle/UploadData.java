package com.xinyuan.handle;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shxy.db.DBHelper;
import com.shxy.db.DBManager;
import com.shxy.exception.DBException;
import com.xy.conf.Configuration;
import com.xy.sender.Sender;
import com.xy.util.FileCopy;
import com.xy.util.FileZip;

/**
 * 交互数据
 *
 * @author Vic.z
 */
@Slf4j
public class UploadData {

    /**
     * 发送数据
     *
     * @param receiver 接收端(单位流水号)
     * @param type     交互类型：
     *                 1	业务
     *                 2	岗位数设置与管理
     *                 3	岗位分布管理
     *                 4	绿色通道设置
     *                 5	数据同步
     *                 6	数据同步反馈
     *                 7	结构比例设置
     *                 8	照片同步
     * @param list     需要执行的语句
     * @return boolean 发送状态（true：成功，false：失败）
     * @throws Exception
     */
    public boolean send(Connection conn, String receiver, String type, List<String> list) throws Exception {
        boolean result = false;

        UploadHelper up = new UploadHelper();
        String realpath = "";
        String remote_ip = "";
        try {
            if (receiver == null || "".equals(receiver)) {
                receiver = Configuration.configInfo.get("remote_Id");
                remote_ip = Configuration.configInfo.get("remote_db_IP");
            } else { //区级单位下发
                remote_ip = this.getRemoteCompanyIP(conn, receiver);
                //如果不存在，则查询局级单位，存在则receiver为局级单位
                if ("".equals(remote_ip)) {
                    receiver = this.getParentCompanyId(conn, receiver);
                    remote_ip = this.getRemoteCompanyIP(conn, receiver);
                }
            }
            realpath = Configuration.configInfo.get("workDir_upload") + receiver + File.separator;
            FileCopy.createFile(realpath, false);
            //同步照片
            if ("8".equals(type)) {
                String zipName = "";
                boolean flag = false;
                if (list.size() > 0) {
                    String photoPath = list.get(0);
                    File fileTemp = new File(photoPath);
                    if (fileTemp.exists()) {
                        zipName = up.getFileName(photoPath, "zip");
                        FileZip.zip(photoPath, realpath + zipName);
                        flag = true;
                    }
                }
                if (flag) {
                    result = new Sender().sendFile(receiver, realpath + zipName, type, conn);
                } else {
                    log4j.error("no photo info to be upload!!");
                }
            } else {
                List<String> tmp = this.getInfo(list, remote_ip);
                if (tmp != null) {
                    String fileName = up.createSqlFile(realpath, tmp);

                    result = new Sender().sendFile(receiver, realpath + fileName, type, conn);
                } else {
                    log4j.error("no info to be upload!!");
                }
            }
        } catch (Exception e) {
            log4j.error("发送数据失败", e);
            e.printStackTrace();
            throw new Exception(e);
        }

        return result;
    }

    /**
     * 构造发送脚本内容
     *
     * @param sqlList 需要执行的sql语句
     * @return 待发送的数据
     * @throws DBException
     */
    public List<String> getInfo(List<String> sqlList, String remote_ip) throws DBException {
        List<String> list = new ArrayList<String>();
        if (sqlList.size() == 0) {
            return null;
        }
//		list.add("conn DBXY/shanghaixinyuan@"+Configuration.configInfo.get("remote_db_IP")+"/XE;");
        String connTitle = "";
        if (remote_ip != null && remote_ip.length() > 0) {
            connTitle = "conn " + Configuration.configInfo.get("remote_user") + "/" + Configuration.configInfo.get("remote_password") + "@" + remote_ip + "/XE;";
//			connTitle="conn DBXY/shanghaixinyuan@"+remote_ip+"/XE;";
        } else {
            connTitle = "conn " + Configuration.configInfo.get("remote_user") + "/" + Configuration.configInfo.get("remote_password") + "@XE;";
//			connTitle="conn DBXY/shanghaixinyuan@XE;";
        }
        list.add(connTitle);
        list.add("declare");
        list.add("temp_ID NUMBER;");
        list.add("temp_num NUMBER;");
        list.add("ids TYPE_ARRAY:=TYPE_ARRAY();");
        list.add("temp_name TYPE_ARRAY:=TYPE_ARRAY();");

        list.add("begin");
        for (String str : sqlList) {
            list.add(str);
        }
        list.add("commit;");
        list.add("end;");
        list.add("/");
        list.add("disconn;");
        list.add("exit;");
        return list;
    }

    /**
     * 获取基层单位的IP
     *
     * @param conn      数据库连接
     * @param companyId 单位id
     * @return
     * @throws DBException
     */
    public String getRemoteCompanyIP(Connection conn, String companyId) throws DBException {
        String remoteIP = "";

        PreparedStatement pre = null;
        ResultSet rs = null;
        try {
            pre = conn.prepareStatement("select TAB_0511_007 from TAB_0511 where TAB_0511_001=?");
            pre.setString(1, companyId);
            rs = pre.executeQuery();
            String[][] info = DBHelper.rsToArray(rs);
            if (info.length > 0) {
                remoteIP = info[0][0];
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pre != null) {
                pre.close();
                pre = null;
            }
        } catch (Exception e) {
            throw new DBException("读取基层单位ip异常", e);
        } finally {
            DBManager.close(pre);
        }

        return remoteIP;
    }

    /**
     * 根据当前单位读取父局级单位id
     *
     * @param conn      数据库连接对象
     * @param companyId 单位id
     * @throws DBException sql语句异常
     */
    public String getParentCompanyId(Connection conn, String companyId) throws DBException {
        String result = "";
        PreparedStatement pre = null;
        ResultSet rs = null;
        try {
            String sql = "with aa AS" +
                    "(SELECT SERIALID,columnname,DATAVALUE" +
                    " FROM " +
                    " (SELECT SERIALID,columnname,DATAVALUE," +
                    " (row_number() over(PARTITION BY SERIALID,columnname ORDER BY STARTTIME DESC,SHOWFLAG DESC,OPERATETIME DESC)) rw" +
                    " FROM TAB_C01" +
                    " WHERE columnname IN('C0100P')" +
                    " AND STATUS=1" +
                    " AND STARTTIME<=to_char(sysdate,'yyyy-mm-dd')" +
                    " )" +
                    " WHERE rw = 1" +
                    " ), " +
                    " bb AS" +
                    " (SELECT MAX(DECODE(columnname,'C0100P',DATAVALUE,'')) C0100P," +
                    " SERIALID" +
                    " FROM aa" +
                    " GROUP BY SERIALID" +
                    " ) " +
                    " select SERIALID,rm from (" +
                    " select SERIALID,C0100P,rownum rm from bb start with SERIALID='" + companyId + "' connect by prior C0100P = SERIALID " +
                    " ) " +
                    " where rm=2" +
                    " ORDER by rownum desc";
            pre = conn.prepareStatement(sql);
            rs = pre.executeQuery();
            String[][] info = DBHelper.rsToArray(rs);
            if (info.length > 0) {
                result = info[0][0];
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (pre != null) {
                pre.close();
                pre = null;
            }
        } catch (Exception e) {
            throw new DBException("读取局级单位异常", e);
        } finally {
            DBManager.close(pre);
        }
        return result;
    }
}