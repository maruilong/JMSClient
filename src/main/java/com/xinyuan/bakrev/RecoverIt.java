package com.xinyuan.bakrev;

import com.xinyuan.comm.MessageBak;
import com.xinyuan.config.ClientConfig;
import com.xinyuan.sender.SendInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理备份文件辅助类
 *
 * @author Vic
 */
@Slf4j
@Component
public class RecoverIt {


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ClientConfig clientConfig;


    /**
     * 重新发送未成功的消息
     */
    public void rec_upload() {
        List<String> list = this.readBakInfo(clientConfig.getMsgBakPath());
        List<String> success = new ArrayList<>();
        if (list.size() == 0) {
            return;
        }
        //重新发送消息
        SendInfo send = new SendInfo();
        String[] tmp;

        for (String str : list) {
            tmp = str.split(",");

            MessageBak messageBak = new MessageBak();
            messageBak.setMsgId(Long.valueOf(tmp[0]));
            messageBak.setReceiver(tmp[1]);
            messageBak.setRecOrExec(tmp[3]);

            send.sendMsgInfo(messageBak);

            success.add(str);
            list.removeAll(success);

            //发送成功，删除文件
            if (list.size() == 0) {
                deleteFile(clientConfig.getMsgBakPath());
            } else {//发送失败，将信息保存
                new BakWrite().bakUpload(list, true);
            }
        }
    }

    /**
     * 重新保存接收的消息至数据库
     *
     * @throws Exception
     */
    public void recSaveInfo() {
        List<String> list = this.readBakInfo(clientConfig.getDbBakPath());
        if (list.size() == 0) {
            return;
        }
        EntityTransaction transaction = entityManager.getTransaction();
        //重新保存消息
        try {
            transaction.begin();
            for (String sql : list) {
                if (!StringUtils.isEmpty(sql)) {
                    Query nativeQuery = entityManager.createNativeQuery(sql);
                    nativeQuery.executeUpdate();
                }
            }
            transaction.commit();
            deleteFile(clientConfig.getDbBakPath());
        } catch (Exception e) {
            if (transaction != null &&
                    transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }

    }

    /**
     * 读取备份信息
     *
     * @return List<String> 备份信息
     */
    @SuppressWarnings("unchecked")
    private List<String> readBakInfo(String path) {

        List<String> list = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return list;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            Object obj = ois.readObject();
            list = (List<String>) obj;
        } catch (Exception e) {
            log.error("error occured when reading the bak file,fileName:" + path, e);
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return list;
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}