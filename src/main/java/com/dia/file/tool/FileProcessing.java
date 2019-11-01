package com.dia.file.tool;

import com.aliyun.oss.OSSClient;
import com.dia.file.enumModule.ContentType;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 开发公司：xxx公司
 * 版权：xxx公司
 * <p>
 * FileProcessing
 *
 * @author 刘志强
 * @created Create Time: 2019/2/14
 */
public class FileProcessing {

    private FileModle fileModle;

    private OSSModle ossModle;

    public FileProcessing(FileModle fileModle,OSSModle ossModle) {
        this.fileModle = fileModle;
        this.ossModle = ossModle;
    }


    public PathModel createFile(MultipartFile file) {
        PathModel pathFile = new PathModel();
        String loadName = file.getOriginalFilename();
        String[] nameArr = loadName.split("\\.");
        String Suffix = nameArr[nameArr.length - 1];
        if (this.fileModle != null && this.fileModle.getPath() != null) {
            DateFormat format = new SimpleDateFormat(this.fileModle.getDirName());
            String dirName = format.format(new Date());
            DateFormat formatFileName = new SimpleDateFormat(this.fileModle.getFileName());
            String imgName = formatFileName.format(new Date());
            imgName = imgName + "." + Suffix;
            try {
                if (!Files.exists(Paths.get(this.fileModle.getPath() + "/" + dirName))) {
                    Files.createDirectories(Paths.get(this.fileModle.getPath() + "/" + dirName));
                }

                imgName = getFileName(this.fileModle.getPath(), dirName, imgName);

                Path path = Files.createFile(Paths.get(this.fileModle.getPath() + "/" + dirName + "/" + imgName));
                Files.write(path, file.getBytes());
                pathFile.setFilePath(this.ossModle.getEndpoint() + this.fileModle.getPath() + "/" + dirName + "/" + imgName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


            if (this.ossModle != null && this.ossModle.getAccessKeyId() != null) {
                OSSClient ossClient = new OSSClient(this.ossModle.getEndpoint(), this.ossModle.getAccessKeyId(), this.ossModle.getAccessKeySecret());
                boolean exists = ossClient.doesBucketExist(this.ossModle.getBucketName());
                // 判断存储空间是否存在，不存在则创建
                if (!exists) {
                    ossClient.createBucket(this.ossModle.getBucketName());
                }

                DateFormat format = new SimpleDateFormat(this.ossModle.getDirName());
                String dirName = format.format(new Date());
                DateFormat formatFileName = new SimpleDateFormat(this.ossModle.getFileName());
                String imgName = formatFileName.format(new Date());
                imgName = imgName + "." + Suffix;

                if (ossClient.doesObjectExist(this.ossModle.getBucketName(), dirName+ "/" + imgName)) {
                    imgName = UUID.randomUUID().toString().replaceAll("-", "") + imgName;
                }
                // 文件上传
                try {
                    ossClient.putObject(this.ossModle.getBucketName(), dirName + "/" + imgName, file.getInputStream());
                    pathFile.setOssPath("https://" + this.ossModle.getBucketName() + ".oss-cn-beijing.aliyuncs.com/" + dirName  + "/" + imgName );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 关闭OSSClient。
                ossClient.shutdown();
            }
        return pathFile;
    }


    /**
     *获取文件名
     */
    public String getFileName(String path, String dirName, String imgName) {

        if (Files.exists(Paths.get(path + "/" + dirName+ "/" + imgName))) {
            imgName = UUID.randomUUID().toString().replaceAll("-", "") + imgName;
            return  getFileName(path, dirName, imgName);
        } else {
            return imgName;
        }
    }

    /**
     * 获取文件
     * @param path 文件地址
     * @return
     * @throws Exception
     */
    public ResponseEntity<byte[]> getFile(String path) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        String [] nameArr = path.split("\\.");
        String type = nameArr[nameArr.length-1];
        String[] pathArr = path.split("/");
        String name = pathArr[pathArr.length - 1];
        ContentType contentType = ContentType.ContentTypeStr(type.toLowerCase());
        headers.setContentType(MediaType.valueOf(contentType.getType()));
        headers.setContentDispositionFormData("attachment", new String(name.getBytes("utf-8"), "ISO8859-1"));
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(Paths.get(path).toFile()), headers, HttpStatus.OK);
    }
}