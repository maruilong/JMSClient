package com.xinyuan.handle;

import java.io.PrintWriter;
import java.util.List;

import com.xinyuan.util.Verify;

/**
 * 文件操作类
 *
 * @author Vic.z
 */
public class ContentResolve {

    private static int maxlen = 2500;
    private static int splitlen = 1000;

    /**
     * 将数据写入文件
     *
     * @param pw   输出流
     * @param list 数据
     */
    public static void writerToFile(PrintWriter pw, List<String> list) {
        if (Verify.isNullObject(pw, list)) {
            return;
        }
        for (String data : list) {
            if (data != null && data.length() > splitlen) {

                int alllength = data.length();
                for (int i = 0; i < alllength; i++) {
                    if (i >= alllength) {
                        pw.println(data.substring(i, data.length()));
                        break;
                    }
                    int pos = postPerstion(i, data);
                    if (pos == 0) {
                        pw.println(data.substring(i, i + 1));
                        i = i + 1;
                    } else {
                        pw.println(data.substring(i, pos + 1));
                        i = pos;
                    }
                }
            } else {
                pw.println(data);
            }
        }
    }

    /**
     * 找到" ",")",","所处的位置
     *
     * @param i    目前循环到的位置
     * @param data 数据
     * @return " ",")",","所处的位置
     */
    private static int postPerstion(int i, String data) {
        int result = 0;
        if (i >= data.length() || i + splitlen >= data.length()) {
            return data.length() - 1;
        }
        String temp = data.substring(i, i + 1);
        if (temp.equals(")") || temp.equals(";") || temp.equals(",")) {
            return i;
        }
        for (int j = i + splitlen - 1; j >= i; j--) {
            if (j == data.length() - 1) {
                return j;
            }
            temp = data.substring(j, j + 1);
            if (temp.equals(")") || temp.equals(";") || temp.equals(",")) {
                return j;
            }
        }
        if (result == 0) {
            for (int k = i + splitlen; k < i + maxlen; k++) {
                if (k == data.length() - 1) {
                    return k;
                }
                temp = data.substring(k, k + 1);
                if (temp.equals(")") || temp.equals(";") || temp.equals(",")) {
                    return k;
                }
            }
        }
        return i + splitlen - 1;
    }
}
