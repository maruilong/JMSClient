package com.xinyuan.bakrev;

import com.xinyuan.config.ClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 备份类，完成文件的写操作
 *
 * @author Vic
 */
@Slf4j
public class BakWrite {
    /**
     * 消息备份文件名
     */
    private String msgBakPath;
    /**
     * 数据库备份文件名
     */
    private String dbBakPath;

    /**
     * 构造器
     */
    public BakWrite() {
        ApplicationContext applicationContext = new StaticWebApplicationContext();
        ClientConfig clientConfig = applicationContext.getBean(ClientConfig.class);
        this.msgBakPath = clientConfig.getBakDir() + "msg_bak.bak";
        this.dbBakPath = clientConfig.getBakDir() + "db_bak.bak";
    }

    /**
     * 备份未发送成功的消息
     *
     * @param info   待备份信息
     * @param append 追加或者覆盖
     */
    public void bakUpload(String info, boolean append) {
        this.bak(this.getList(info), msgBakPath, append);
    }

    /**
     * 备份未发送成功的消息
     *
     * @param info   待备份信息
     * @param append 追加或者覆盖
     */
    public void bakUpload(List<String> info, boolean append) {
        this.bak(info, msgBakPath, append);
    }

    /**
     * 备份未成功存入数据库的信息
     *
     * @param info   待备份信息
     * @param append 追加或者覆盖
     */
    public void bakDbInfo(String info, boolean append) {
        this.bak(this.getList(info), dbBakPath, append);
    }

    /**
     * 转换为List
     *
     * @param info 要备份的信息
     * @return List 存放备份信息的List
     */
    private List<String> getList(String info) {
        List<String> list = new ArrayList<>();
        list.add(info);
        return list;
    }

    /**
     * 备份信息
     *
     * @param info 信息
     * @param path 备份路径
     * @param flag 追加或者覆盖
     */
    @SuppressWarnings("unchecked")
    private void bak(List<String> info, String path, boolean flag) {
        if (info == null) {
            info = new ArrayList<>();
        }
        //追加，但是无信息，直接返回
        if (info.size() == 0 && flag) {
            return;
        }
        boolean needWrite = false;

        File file = new File(path);
        List<String> allInfo = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            //文件不存在
            if (!file.exists()) {
                if (info.size() != 0) {
                    file.createNewFile();
                    allInfo = info;
                    needWrite = true;
                }
            } else {//文件存在
                ois = new ObjectInputStream(new FileInputStream(file));
                Object obj = ois.readObject();
                allInfo = (List<String>) obj;
                //原内容为空
                if (allInfo == null || allInfo.size() == 0) {
                    //有新内容，无论是追加还是覆盖都要写入
                    if (info.size() != 0) {
                        allInfo = info;
                        needWrite = true;
                    }
                } else {//内容不为空
                    if (info.size() != 0) {
                        //有内容要写入
                        if (flag) {
                            //追加写入
                            allInfo.addAll(info);
                        } else {//覆盖写入
                            allInfo = info;
                        }
                        needWrite = true;
                    } else {//无内容
                        if (!flag) {
                            allInfo = info;
                            needWrite = true;
                        }
                    }
                }
            }
            //需要写入
            if (needWrite) {
                oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(allInfo);
                oos.flush();
            }
        } catch (Exception e) {
            log.error("fatal...bakup failed, error detail:", e);
            log.error("--------------msg detail--------------begin------");
            for (int i = 0; i < info.size(); i++) {
                log.error("fatal...bakup failed, msg: " + info.get(i) + " . error detail:", e);
            }
            log.error("--------------msg detail--------------begin------");
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                log.error("close I/O stream failed,detail:", e);
            }
        }

    }
}