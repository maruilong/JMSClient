package com.xinyuan.bakrev;

import com.xinyuan.config.ClientConfig;
import com.xinyuan.entity.DownloadInfo;
import com.xinyuan.entity.UploadInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DownloadInfoBak {

    @Autowired
    private ClientConfig clientConfig;

    public void bak(DownloadInfo downloadInfo) throws IOException {
        String path = clientConfig.getDbBakPath();
        Long id = downloadInfo.getId();
        String companyId = downloadInfo.getCompanyId();

        File file = new File(path + File.separator + id + "_" + companyId + ".object");

        ObjectOutput output = new ObjectOutputStream(new FileOutputStream(file));

        output.writeObject(downloadInfo);

        output.flush();
        output.close();
    }

    public List<DownloadInfo> read() throws IOException, ClassNotFoundException {
        List<DownloadInfo> downloadInfos = new ArrayList<>();

        String path = clientConfig.getDbBakPath();
        File file = new File(path);

        File[] listFiles = file.listFiles();

        for (File listFile : listFiles) {
            if (listFile.getName().endsWith(".object")) {
                ObjectInput input = new ObjectInputStream(new FileInputStream(listFile));

                DownloadInfo downloadInfo = (DownloadInfo) input.readObject();
                downloadInfos.add(downloadInfo);
            }
        }
        return downloadInfos;
    }
}
