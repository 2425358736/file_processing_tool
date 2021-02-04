package com.dia.file.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 开发公司：联信
 * 版权：联信
 * <p>
 * Annotation
 *
 * @author 刘志强
 * @created Create Time: 2021/1/29
 */
@Slf4j
public class ExcelAnalysis {

    private ExcelAnalysis() {
    }

    private static final String XLS = "xls";

    /**
     * 单对象映射
     *
     * @param file
     * @param listField
     * @param cla
     * @return
     */
    public static <T> List<T> parseExcel(MultipartFile file, List<String> listField, Class<T> cla) {
        try {
            return parseExcel(file.getInputStream(), file.getOriginalFilename(), listField,cla );
        } catch (IOException e) {
            log.error("io异常{}",e.getMessage());
            return null;
        }
    }

    public static <T> List<T> parseExcel(InputStream inputStream, String fileName, List<String> listField, Class<T> cla) {
        List<T> list = new ArrayList<>();
        Workbook work;
        String[] str = fileName.split("\\.");
        try {
            if (StringUtils.equals(str[str.length - 1], XLS)) {
                work = new HSSFWorkbook(inputStream);
            } else {
                work = new XSSFWorkbook(inputStream);
            }
            if (null == work) {
                log.error("创建work失败");
                return null;
            }
        } catch (IOException e) {
            log.error("io异常{}", e.getMessage());
            return null;
        }
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            Sheet sheet = work.getSheetAt(i);
            for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }
                // 反射出的对象
                T object;
                // 对象的所有属性
                Field[] fields;
                try {
                    object = cla.newInstance();
                    fields = cla.getDeclaredFields();

                } catch (Exception e) {
                    log.error("异常：反射对象失败{}", e.getMessage());
                    return null;
                }
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum() && y < listField.size(); y++) {
                    Cell cell = row.getCell(y);
                    Boolean on = true;
                    for (Field field : fields) {
                        if (StringUtils.equals(listField.get(y), field.getName())) {
                            on = false;
                            try {
                                Class<?> aClass = field.getType();
                                // 构建属性值
                                Object val = aClass.getConstructor(String.class).newInstance(cell.getStringCellValue());
                                // 开启私有方法访问
                                field.setAccessible(true);
                                field.set(object, val);
                            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                                log.error("属性赋值异常：{}", e.getMessage());
                            } catch (NoSuchMethodException e) {
                                // 没有String的构造方法 对象引用类型
                                log.error("没有String的构造方法 对象引用类型");
                            }
                        }
                    }
                    if (Boolean.TRUE.equals(on)) {
                        log.error("没有{}属性",listField.get(y));
                    }
                }
                list.add(object);
            }
        }
        return list;
    }
}
