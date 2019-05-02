package com.lxc.mall2.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 82138 on 2018/8/24.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);

}
