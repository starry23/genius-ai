/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package cn.apeto.geniusai.server.service.oss;


import cn.apeto.geniusai.server.utils.CommonUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 文件上传Factory
 */
public final class OSSFactory {
    private OSSFactory() {
    }

    private static CloudStorageService service;

    private static Integer type;

    public static CloudStorageService build() {
        if (service == null) {
            CloudStorageConfig config = CommonUtils.getCloudStorageConfig();
            type = config.getType();
            service = getServicesByType(type);
        }
        return service;
    }

    public static Integer getType() {
        return type;
    }

    public static void destroy() {
        service = null;
        type = null;
    }



    public static CloudStorageService getServicesByType(Integer type) {
        CloudStorageService service;
        CloudStorageConfig config = CommonUtils.getCloudStorageConfig();
        if (type == CloudService.LOCAL.getValue()) {
            service = new LocalStorageService(config);
        } else if (type == CloudService.QINIU.getValue()) {
            service = new QiniuCloudStorageService(config);
        } else if (type == CloudService.ALIYUN.getValue()) {
            service = new AliyunCloudStorageService(config);
        } else if (type == CloudService.QCLOUD.getValue()) {
            service = new QcloudCloudStorageService(config);
        } else {
            throw new IllegalArgumentException("不支持的存储类型");
        }
        return service;
    }

    public enum CloudService {
        /**
         * 本地
         */
        LOCAL(0, "本地"),
        QINIU(1, "七牛云"),
        /**
         * 阿里云
         */
        ALIYUN(2, "阿里云"),
        /**
         * 腾讯云
         */
        QCLOUD(3, "腾讯云");

        private int value;

        private String name;

        CloudService(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static CloudService getByValue(Integer value) {
            Optional<CloudService> first = Stream.of(CloudService.values()).filter(cs -> value.equals(cs.value)).findFirst();
            if (!first.isPresent()) {
                throw new IllegalArgumentException("非法的枚举值:" + value);
            }
            return first.get();
        }
    }
}
