package com.xinyuan.handle;

import java.lang.reflect.Constructor;

/**
 * @author shxy
 */
public class Factory {


    public static IHandler getInstance(String className) {
        try {
            Class<?> tmp = Class.forName(className);
            Constructor<?> con = tmp.getConstructor();
            if (!con.isAccessible()) {
                con.setAccessible(true);
            }
            return (IHandler) con.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
