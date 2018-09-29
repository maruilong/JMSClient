package com.xinyuan.handle;

import com.xinyuan.comm.Helper;
import com.xinyuan.exception.FileOperationException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liang
 */
@Slf4j
@Component
public class FileResolve extends Helper {

    @Autowired
    private EntityManager entityManager;


    public List<String> execute(String filePath) throws Exception {
        List<String> backSql = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileOperationException("目录位置错误!");
        }
        String fileName = file.getName();

        /** xml文件的一条xml语句生成一条 */
        SAXReader saxReader = new SAXReader();
        /** 读取对象 */
        Document document;
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new Exception("error occured when create dml script,fileName:" + fileName, e);
        }
        Element rootElement = document.getRootElement();

        rootElement.attribute("clientId").getValue();

        List<Element> elements = rootElement.elements();

        //里面是一个一个表
        for (Element element : elements) {
            String tableName = element.attribute("id").getValue();
            List<Element> innerElements = element.elements();
            for (Element innerElement : innerElements) {
                //局级数据id
                String baseId = innerElement.attribute("id").getValue();
                //局级同步表id
                String synId = innerElement.attribute("synid").getValue();

                String type = "1";
                Attribute attribute = innerElement.attribute("type");
                if (attribute != null) {
                    type = attribute.getValue();
                }
                if ("1".equals(type)) {
                    int flag = 0;

                    //增加数据
                    try {
                        //数据表
                        String SERIALID = innerElement.element("SERIALID").getText();
                        String DATAVALUE = innerElement.element("DATAVALUE").getText();
                        String STATUS = innerElement.element("STATUS").getText();
                        String COLUMNNAME = innerElement.element("COLUMNNAME").getText();
                        String STARTTIME = innerElement.element("STARTTIME").getText();
                        String OPERATETIME = innerElement.element("OPERATETIME").getText();
                        String OPERATOR = innerElement.element("OPERATOR").getText();
                        String SHOWFLAG = innerElement.element("SHOWFLAG").getText();
                        String CID = innerElement.element("CID").getText();
                        String pkid = "";
                        if (innerElement.element("PKID") != null) {
                            pkid = innerElement.element("PKID").getText();
                        }
                        String sql = "select id from " + tableName + " where baseid='" + baseId + "' and cid='" + CID + "'";
                        Query nativeQuery = entityManager.createNativeQuery(sql);
                        //获得结果数量
                        int results = nativeQuery.getMaxResults();

                        if (1 == results) {
                            //有数据 执行更新操作
                            StringBuffer updateSql = new StringBuffer("update " + tableName + " set SERIALID='" + SERIALID + "',DATAVALUE='" + DATAVALUE + "',STATUS='" + STATUS + "',COLUMNNAME='" + COLUMNNAME + "',STARTTIME='" + STARTTIME + "',OPERATETIME=to_date('" + OPERATETIME + "','yyyy-MM-dd hh24:mi:ss'),OPERATOR='" + OPERATOR + "',SHOWFLAG='" + SHOWFLAG + "',CID='" + CID + "'");

                            if (!"".equals(pkid)) {
                                updateSql.append(",pkid='" + pkid + "'");
                            }
                            updateSql.append(" where baseid='" + baseId + "' and cid='" + CID + "'");

                            Query updateQuery = entityManager.createNativeQuery(updateSql.toString());

                            updateQuery.executeUpdate();
                        } else if (2 == results) {
                            flag = 1;
                        } else if (0 == results) {
                            StringBuffer insertSql = new StringBuffer();
                            if (StringUtils.isEmpty(pkid)) {
                                insertSql.append("insert into " + tableName + "(SERIALID,DATAVALUE,STATUS,COLUMNNAME,STARTTIME,OPERATETIME,OPERATOR,SHOWFLAG,CID,baseid) values('" + SERIALID + "','" + DATAVALUE + "','" + STATUS + "','" + COLUMNNAME + "','" + STARTTIME + "',to_date('" + OPERATETIME + "','yyyy-MM-dd hh24:mi:ss'),'" + OPERATOR + "','" + SHOWFLAG + "','" + CID + "','" + baseId + "')");
                            } else {
                                insertSql.append("insert into " + tableName + "(SERIALID,DATAVALUE,STATUS,COLUMNNAME,STARTTIME,OPERATETIME,OPERATOR,SHOWFLAG,CID,baseid,pkid) values('" + SERIALID + "','" + DATAVALUE + "','" + STATUS + "','" + COLUMNNAME + "','" + STARTTIME + "',to_date('" + OPERATETIME + "','yyyy-MM-dd hh24:mi:ss'),'" + OPERATOR + "','" + SHOWFLAG + "','" + CID + "','" + baseId + "','" + pkid + "')");
                            }

                            Query insert = entityManager.createNativeQuery(insertSql.toString());
                            insert.executeUpdate();
                        }

                        String returnSql = "";
                        if (flag == 1) {
                            returnSql = "update TAB_SYN set TAB_SYN_005='数据违反唯一性约束' where id='" + synId + "';";
                        } else {
                            returnSql = "delete from TAB_SYN where id='" + synId + "';";
                        }
                        backSql.add(returnSql);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //删除数据
                    try {
                        String sql = "delete from " + tableName + " where baseid='" + baseId + "'";
                        Query nativeQuery = entityManager.createNativeQuery(sql);
                        nativeQuery.executeUpdate();

                        String returnSql = "delete from TAB_SYN where id='" + synId + "';";
                        backSql.add(returnSql);

                    } catch (Exception e) {
                        log.error("执行数据同步脚本失败!");
                        e.printStackTrace();
                    }


                }

            }
        }
        return backSql;
    }
}
