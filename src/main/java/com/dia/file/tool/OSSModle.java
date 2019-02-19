package com.dia.file.tool;

import lombok.Data;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * OSSModle
 *
 * @author 刘志强
 * @created Create Time: 2019/2/18
 */
@Data
public class OSSModle {
    /**
     * 端点服务地址
     */
    private String endpoint;
    /**
     * accessKeyId
     */
    private String accessKeyId;
    /**
     * accessKeySecret
     */
    private String accessKeySecret;
    /**
     * 包名
     */
    private String bucketName;

    /**
     * 文件名策略 日期格式，
     */
    private String fileName;

    /**
     * 文件夹策略 日期格式，
     */
    private String dirName;
}