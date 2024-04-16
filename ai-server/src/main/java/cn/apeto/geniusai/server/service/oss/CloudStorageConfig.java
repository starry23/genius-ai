/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package cn.apeto.geniusai.server.service.oss;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 云存储配置信息
 */
@Data
public class CloudStorageConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    //类型 1：七牛  2：阿里云  3：腾讯云
    @Schema(description = "oss类型")
    private Integer type;

    //七牛绑定的域名
    @Schema(description = "七牛绑定的域名")
    private String qiniuDomain;
    //七牛路径前缀
    @Schema(description = "七牛路径前缀")
    private String qiniuPrefix="genius_ai";
    //七牛ACCESS_KEY
    @Schema(description = "七牛ACCESS_KEY")
    private String qiniuAccessKey;
    //七牛SECRET_KEY
    @Schema(description = "七牛SECRET_KEY")
    private String qiniuSecretKey;
    //七牛存储空间名
    @Schema(description = "七牛存储空间名")
    private String qiniuBucketName;

    //阿里云绑定的域名
    @Schema(description = "阿里云绑定的域名")
    private String aliyunDomain;
    //阿里云路径前缀
    @Schema(description = "阿里云路径前缀")
    private String aliyunPrefix="genius_ai";
    //阿里云EndPoint
    @Schema(description = "阿里云EndPoint")
    private String aliyunEndPoint;
    //阿里云AccessKeyId
    @Schema(description = "阿里云AccessKeyId")
    private String aliyunAccessKeyId;
    //阿里云AccessKeySecret
    @Schema(description = "阿里云AccessKeySecret")
    private String aliyunAccessKeySecret;
    //阿里云BucketName
    @Schema(description = "阿里云BucketName")
    private String aliyunBucketName;

    //腾讯云绑定的域名
    @Schema(description = "腾讯云绑定的域名")
    private String qcloudDomain;
    //腾讯云路径前缀
    @Schema(description = "腾讯云路径前缀")
    private String qcloudPrefix="genius_ai";
    //腾讯云AppId
    @Schema(description = "腾讯云AppId")
    private String qcloudAppId;
    //腾讯云SecretId
    @Schema(description = "腾讯云SecretId")
    private String qcloudSecretId;
    //腾讯云SecretKey
    @Schema(description = "腾讯云SecretKey")
    private String qcloudSecretKey;
    //腾讯云BucketName
    @Schema(description = "腾讯云BucketName")
    private String qcloudBucketName;
    //腾讯云COS所属地区
    @Schema(description = "腾讯云COS所属地区")
    private String qcloudRegion;


    @Schema(description = "域名(静态资源域名)")
    private String localDomain;
    @Schema(description = "路径前缀")
    private String localPrefix="imgs";
    @Schema(description = "根目录")
    private String localRootPath;


}
