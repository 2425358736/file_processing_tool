package com.dia.file.tool;


import lombok.Data;

/**
 * 开发公司：xxx公司
 * 版权：xxx公司
 * <p>
 * FileModle
 *
 * @author 刘志强
 * @created Create Time: 2019/2/16
 */

@Data
public class FileModle {

    /**
     * 文件路径地址
     */
    private String path;

    /**
     * 文件名策略 日期格式，
     */
    private String fileName;

    /**
     * 文件夹策略 日期格式，
     */
    private String dirName;

    /**
     * 请求地址
     */
    private String endpoint;
}