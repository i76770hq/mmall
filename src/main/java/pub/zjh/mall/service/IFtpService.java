package pub.zjh.mall.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IFtpService {

    /**
     * 上传文件
     *
     * @param remotePath 上传到ftp服务器中的哪个目录下
     * @param fileList
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String remotePath, List<File> fileList) throws IOException;

}
