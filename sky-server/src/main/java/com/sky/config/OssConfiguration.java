package com.sky.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类，用于初始化AliOssUtil
 */
@Configuration
@Slf4j
public class OssConfiguration {


    @Bean
    @ConditionalOnMissingBean
    // properties从配置文件读取那4个参数然后封装成一个AliOssProperties对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建云文件上传工具类对象:{}", aliOssProperties);
        // 创建AliOssUtil实例
        return new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
        
    }
}


