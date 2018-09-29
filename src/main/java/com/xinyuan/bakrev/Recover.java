package com.xinyuan.bakrev;

/**
 * @author liang
 */
public class Recover {

    /**
     * 重新保存接收的消息至数据库
     */
    public void recSaveInfo() {
        DBRecover dbRecover = new DBRecover();
        dbRecover.start();
        try {
            dbRecover.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
