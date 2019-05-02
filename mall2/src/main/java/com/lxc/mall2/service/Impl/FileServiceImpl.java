package com.lxc.mall2.service.Impl;

import com.google.common.collect.Lists;
import com.lxc.mall2.service.IFileService;
import com.lxc.mall2.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by 82138 on 2018/8/24.
 */

@Service("iFileService")
public class FileServiceImpl implements IFileService {
    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String filename = file.getOriginalFilename();
        String fileExtensionName = filename.substring(filename.lastIndexOf("."));
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始长传文件，上传文件名:{},文件放置服务器的路径:{},新文件名:{}",filename,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()) {
           fileDir.setWritable(true);
            fileDir.mkdirs();

        }
        File targetFile = new File(path,uploadFileName);
        try {
            //上传到Tomcat服务器文件夹
            file.transferTo(targetFile);
            //从tomcat传到FTP文件夹
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //从tomcat删除掉
            //targetFile.delete();

        } catch (IOException e) {
           logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
