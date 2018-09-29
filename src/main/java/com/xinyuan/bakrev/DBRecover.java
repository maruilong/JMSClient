package com.xinyuan.bakrev;

import com.xinyuan.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 数据库备份信息还原
 *
 * @author Administrator
 */
@Slf4j
@Component
public class DBRecover extends Thread {

    @Override
    public void run() {
        RecoverIt recoverIt = SpringUtil.getBean(RecoverIt.class);
        DBLock dbLock = DBLock.getInstance();
        while (true) {
            try {
                if (dbLock.isLocked()) {
                    sleep(5000);
                } else {
                    if (dbLock.lock()) {
                        try {
                            recoverIt.recSaveInfo();
                            break;
                        } catch (Exception e) {
                            log.error("recover db error,detail:", e);
                            sleep(10000);
                        } finally {
                            dbLock.release();
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }
}
