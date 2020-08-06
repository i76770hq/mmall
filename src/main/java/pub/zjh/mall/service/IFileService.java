package pub.zjh.mall.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {

    /**
     * 上传文件
     *
     * @param multipartFile
     * @param path          上传到/resources本地的哪个目录下
     * @param ftpPath       上传到ftp服务器中的哪个目录下
     * @return
     * @throws IOException
     */
    public String uploadFTP(MultipartFile multipartFile, String path, String ftpPath) throws IOException;

}
