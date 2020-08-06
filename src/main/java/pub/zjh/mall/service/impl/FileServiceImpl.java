package pub.zjh.mall.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pub.zjh.mall.service.IFileService;
import pub.zjh.mall.service.IFtpService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {

    @Autowired
    private IFtpService ftpService;

    @Override
    public String uploadFTP(MultipartFile multipartFile, String path, String ftpPath) throws IOException {
        //将文件保存到resources/upload目录下
        String fileName = multipartFile.getOriginalFilename();
        String extentFile = fileName.substring(fileName.lastIndexOf(".") + 1);

        String uploadFileName = UUID.randomUUID().toString() + "." + extentFile;

        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.setWritable(true);
            uploadDir.mkdirs();
        }

        File targetFile = new File(path, uploadFileName);
        multipartFile.transferTo(targetFile);

        //上传到ftp服务器中
        ftpService.uploadFile(ftpPath, Lists.newArrayList(targetFile));

        //删除resources/upload目录下文件
        targetFile.delete();

        return targetFile.getName();
    }

}
