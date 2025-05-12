package com.sky.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        // 上传文件到阿里云OSS

        try {
            // 获取原始文件名
            String originalFileName = file.getOriginalFilename();
            // 获取文件后缀名
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 生成加密新的文件名
            String objectName = UUID.randomUUID().toString()+ extension;
            // 文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) { 
            log.error("文件上传失败：{}", e);
            e.printStackTrace();
        }
        
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
