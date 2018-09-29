package com.xinyuan.handle;


import com.xinyuan.entity.DownloadInfo;
import org.springframework.stereotype.Component;

/**
 * �������صõ�����Ϣ
 *
 * @author shxy
 */
@Component
public interface IHandler {

    /**
     * ������������Ϣ
     *
     * @param downloadInfo ���صõ�����Ϣ����
     * @return String �ýű�������sql�ļ���·��
     * @throws Exception
     */
    String handle(DownloadInfo downloadInfo) throws Exception;
}
