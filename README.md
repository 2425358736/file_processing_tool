使用：
    
```
<dependency>
    <groupId>com.dia.file</groupId>
    <artifactId>file_processing_tool</artifactId>
    <version>1.0.2</version>
</dependency>
```

### 文件上传
   
1. 实例化 FileProcessing 类

```bash
采用@Bean 注解实例化FileProcessing类

1. application.yml文件中配置上传信息

file:
  fileModle: # 本地上传信息 
    path: file # 文件上传地址
    fileName: yyyy-MM-dd-HH-mm-ss # 文件名规则
    dirName: yyyy-MM-dd # 文件目录规则
  ossModel: # oss上传信息
    endpoint: https://oss-cn-beijing.aliyuncs.com # oss Endpoint
    accessKeyId: xxxxxxxxxxx # oss accessKeyId
    accessKeySecret: xxxxxxxxxx # oss accessKeySecret
    bucketName: yun-cunchu # 存储空间
    dirName: yyyy-MM-dd # 文件目录规则
    fileName: yyyy-MM-dd-HH-mm-ss # 文件名规则
    
2. 创建配置类
package com.dolphin.sigle.root.config.tool;

import com.dia.file.tool.FileModle;
import com.dia.file.tool.FileProcessing;
import com.dia.file.tool.OSSModle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * FileProcessingConfig
 *
 * @author 刘志强
 * @created Create Time: 2019/2/16
 */
@Configuration
public class FileProcessingConfig {

    @Bean(name = "fileModle")
    @ConfigurationProperties(prefix = "file.fileModle")
    public FileModle fileModle() {
        FileModle fileModle = new FileModle();
        return  fileModle;
    }

    @Bean(name = "ossModle")
    @ConfigurationProperties(prefix = "file.ossModel")
    public OSSModle ossModle() {
        OSSModle ossModle = new OSSModle();
        return  ossModle;
    }

    @Bean(name = "fileProcessing")
    public FileProcessing fileProcessing(FileModle fileModle, OSSModle ossModle) {
        FileProcessing fileProcessing = new FileProcessing(fileModle,ossModle);
        return  fileProcessing;
    }
}
```




2. 调用 createFile 方法


```

    @PostMapping("/fileUploader")
    public CommonResult<PathModel> fileUploader(MultipartFile file) {
        PathModel path = fileProcessing.createFile(file);
        return CommonResult.successReturn(path,"上传成功");
    }
```


### 文件下载

```
 // filePath文件地址
 ResponseEntity<byte[]> responseEntity = fileProcessing.getFile(filePath);
```


### PoiExcel  excel处理类


```
package com.dolphin.common.tool;

import lombok.Data;

import java.util.List;

/**
 * 开发公司：青岛海豚数据技术有限公司
 * 版权：青岛海豚数据技术有限公司
 * <p>
 * User
 *
 * @author 刘志强
 * @created Create Time: 2019/1/30
 */
@Data
public class User{
    private String userName;
    private Integer age;
    private String gender;
    private Integer nation;
    private List<String> list;
    private User user;
}
```

```
package com.dolphin.common.tool;

import com.dolphin.common.file.PoiExcel;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.junit.Test;

import java.util.*;

public class PoiExcelTest {
    @Test
    public void exportExcel() throws Exception {
        List<Map<String,Object>> listHead = new ArrayList<>();
//        Map<String, Object> mapHead = new HashMap<>();
//        mapHead.put("title", "姓名");
//        mapHead.put("column", "userName");
//        listHead.add(mapHead);
//        Map<String, Object> mapHead1 = new HashMap<>();
//        mapHead1.put("title", "年龄");
//        mapHead1.put("column", "age");
//        listHead.add(mapHead1);
//        Map<String, Object> mapHead2 = new HashMap<>();
//        mapHead2.put("title", "性别");
//        mapHead2.put("column", "gender");
//        listHead.add(mapHead2);
//        Map<String, Object> mapHead3 = new HashMap<>();
//        mapHead3.put("title", "民族");
//        mapHead3.put("column", "nation");
//        JSONObject json = JSONObject.fromObject("{0: '汉族', '1': '少数民族'}");
//        mapHead3.put("columnStr", json);
//        listHead.add(mapHead3);
        // 或者
        String jsonStr = "{listHead: [" +
                "{title: '姓名',column: 'userName'}," +
                "{title: '年龄',column: 'age'}," +
                "{title: '性别',column: 'gender'}," +
                "{title: '民族',column: 'nation', columnStr: {0: '汉族', '1': '少数民族'}}" +
                "]}";
        JsonConfig jsonConfig = new JsonConfig();
        JSONObject json = JSONObject.fromObject(jsonStr,jsonConfig);
        listHead = (List<Map<String, Object>>) json.get("listHead");


        List listDataSource = new ArrayList<>();
        Map<String, Object> mapDataSource = new HashMap<>();
        mapDataSource.put("userName", "张三");
        mapDataSource.put("age", "18");
        mapDataSource.put("gender", "男");
        listDataSource.add(mapDataSource);
        User user = new User();
        user.setUserName("李四");
        user.setGender("男");
        listDataSource.add(user);
        User user1 = new User();
        user1.setUserName("李红");
        user1.setAge(16);
        user1.setGender("女");
        user1.setNation(0);
        listDataSource.add(user1);

        String catalog = "aaa/bbb/ccc/";
        String fileName = "eee";
        System.out.println(
                PoiExcel.exportExcel(listHead,listDataSource,catalog,fileName)
        );
    }

}

```
### Excel解析
```
        String fileName = "D://导入模板.xlsx";
        InputStream inputStream = new FileInputStream(fileName);


        List<String> list = new ArrayList<>();
        list.add("name"); // 第一列
        list.add("ddd"); // 第二列
        List<Test> list1 = ExcelAnalysis.parseExcel(inputStream,fileName,list, Test.class);
        list1.forEach(test -> {
            log.info(test.toString());
        });
```
 
### PoiExcel.exportExcel(listHead,listDataSource,fileName) 导出
### PoiExcel.exportExcel(listHead,listDataSource,catalog,fileName) 创建


### PoiWord word处理类

```
public ResponseEntity<byte[]> wordReplace(String fileName) throws IOException {
        String filePath = "./file/model/test.docx";
        Map<String,Object> map = new HashMap<>();
        map.put("userName", "刘志强");
        map.put("age", "12");
        return PoiWord.wordReplace(filePath,map,fileName);
    }
```

###  PoiWord.wordReplace(filePath,map,fileName); word替换并下载
###  PoiWord.poiTransformation(filePath,map); 返回替换后的字节




