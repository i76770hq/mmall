package pub.zjh.mall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zjh.mall.config.FtpConfig;
import pub.zjh.mall.service.IFtpService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FtpServiceImpl implements IFtpService {

    @Autowired
    private FtpConfig ftpConfig;

    @Override
    public boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
        boolean isUploadSuccess = true;
        FTPClient ftpClient = null;
        try {
            ftpClient = connectFtp();
            if (ftpClient == null) {
                return false;
            }

            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //设置被动模式
            ftpClient.enterLocalPassiveMode();

            for (File file : fileList) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ftpClient.storeFile(file.getName(), fis);
                } catch (Exception e) {
                    log.error("{}", e);
                }
            }
        } catch (Exception e) {
            isUploadSuccess = false;
            log.error("{}", e);
        } finally {
            ftpClient.disconnect();
        }
        return isUploadSuccess;
    }

    private FTPClient connectFtp() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpConfig.getIp());
        if (!ftpClient.login(ftpConfig.getUser(), ftpConfig.getPass())) {
            return null;
        }
        return ftpClient;
    }

}
