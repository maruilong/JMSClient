package com.xinyuan.util;

import java.util.regex.Pattern;

/**
 * 常用验证方法
 *
 * @author shxy
 */
public class Verify {
    /***
     * 判断参数是否为null
     * @param args 参数对象
     * @return 如果为null返回true 如果不为null 返回false
     */
    public static boolean isNullObject(Object... args) {
        if (args == null) {
            return true;
        }
        for (Object obj : args) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String) {
                String str = obj.toString();
                if (str.trim().length() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * 判断参数是否为空的一维维数组对象
     * @param args 多个一维数组
     * @return 如果为null返回true 如果不为null 返回false
     */
    public static boolean isNull1DArray(Object[]... args) {
        if (args == null) {
            return true;
        }
        for (Object obj : args) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String[]) {
                String[] temp = (String[]) obj;
                if (temp.length == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * 判断参数是否为空的一维维数组对象
     * @param args 多个一维数组
     * @return 如果为null返回true 如果不为null 返回false
     */
    public static boolean isNull1OrArray(Object[]... args) {
        if (args == null) {
            return true;
        }
        for (Object obj : args) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String[]) {
                String[] temp = (String[]) obj;
                for (String str : temp) {
                    if (null == str || "".equals(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /***
     * 判断参数是否为空的二维数组对象
     * @param args 多个二维数组
     * @return 如果为null返回true 如果不为null 返回false
     */
    public static boolean isNull2DArray(Object[][]... args) {
        if (args == null) {
            return true;
        }
        for (Object obj : args) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String[][]) {
                String[][] temp = (String[][]) obj;
                if (temp.length == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断字符串是否由纯数字组成
     *
     * @param str 源字符串
     * @return true是，false否
     */
    public static boolean isNumeric(String str) {
        if (Verify.isNullObject(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否是数字
     *
     * @param number 源字符串
     * @return true是，false否
     */
    public static boolean isNumber(String number) {
        //判断参数
        if (Verify.isNullObject(number)) {
            return false;
        }
        //查看是否有小数点
        int index = number.indexOf(".");
        if (index < 0) {
            return isNumeric(number);
        } else {
            //如果有多个".",则不是数字
            if (number.indexOf(".") != number.lastIndexOf(".")) {
                return false;
            }
            String num1 = number.substring(0, index);
            String num2 = number.substring(index + 1);
            return isNumeric(num1) && isNumeric(num2);
        }
    }
}
