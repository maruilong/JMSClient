package com.xinyuan.bakrev;
/**
 * 对文件加锁，防止多线程同时访问
 * @author Administrator
 *
 */
public class DBLock {
    
    private static DBLock dbLock;   
    /**
     * 构造器
     *
     */
    private DBLock(){}
    /**
     * 获取DBLock对象
     * @return DBLock对象
     */
    synchronized public static DBLock getInstance(){
        if(dbLock==null){
            dbLock=new DBLock();
        }
        return dbLock;
    } 
    /**
     * 标识文件是否锁定
     */
    private boolean locked=false;
    
    /**
     * 判断是否锁定
     * @return 是否被锁定
     */
    public boolean isLocked(){
        synchronized (this) {
            return this.locked;
        }
    }
    /**
     * 加锁
     */
    public boolean lock(){
        synchronized (this) {
            if(this.locked){
                return false;
            }else{
                locked=true;
                return true;
            }
        }
    }
    /**
     * 释放锁
     */
    public void release(){
        synchronized (this) {
            this.locked=false;
        }
    }
}
