package com.dia.file.tool;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 开发公司：青岛上朝信息科技有限公司
 * 版权：青岛上朝信息科技有限公司
 * <p>
 * 类功能描述
 *
 * @author 刘志强
 * @created 2018/5/10.
 */
public class PoiExcel {
    public static String exportExcel(List<Map<String,Object>> listHead,List<Objects> listDataSource, String catalog, String fileName){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dirName = format.format(new Date());
        fileName = System.currentTimeMillis() + fileName + ".xls";
        Path path = null;
        try {
            if(!Files.exists(Paths.get(catalog + dirName))){
                Files.createDirectories(Paths.get(catalog + dirName));
            }
            path = Files.createFile(Paths.get(catalog + dirName + "/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建Excel文档
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 创建一个Excel表单,参数为sheet的名字
        HSSFSheet sheet = workbook.createSheet("sheet1");

        // .创建一行
        HSSFRow headerRow = sheet.createRow(0);

        forEach(listHead, (index, mapHead) -> {
            if (mapHead.get("column") != null) {
                HSSFCell cell = headerRow.createCell(index);
                cell.setCellValue(mapHead.get("title").toString());
            }
        });
        int index = 0;
        for (Object obj: listDataSource){
            index++;
            HSSFRow headerRowL = sheet.createRow(index);
            Map<String, Object> map = PoiWord.getFiledsInfo(obj);
            int finalIndex1 = index;
            map.forEach((k, v)->{
                int finalIndex = finalIndex1;
                forEach(listHead, (i, mapHead) -> {
                    if (mapHead.get("column") != null) {
                        if (StringUtils.equals(k, mapHead.get("column").toString())) {
                            HSSFCell cell = headerRowL.createCell(i);
                            if(mapHead.get("columnStr") != null) {
                                Map<String,String> columnStrMap = (Map<String, String>) mapHead.get("columnStr");
                                final boolean[] on = {true};
                                columnStrMap.forEach((k1,v1)->{
                                    if (StringUtils.equals(v.toString(),k1)) {
                                        on[0] = false;
                                        cell.setCellValue(v1.toString());
                                    }
                                });
                                if (on[0]) {
                                    cell.setCellValue(v.toString());
                                }
                            } else if (StringUtils.equals(mapHead.get("column").toString(),"id")){;
                                cell.setCellValue(finalIndex);
                            } else {
                                    cell.setCellValue(v.toString());
                            }
                        }
                    }
                });
            });
        }
        try {
            OutputStream baos = new FileOutputStream(path.toAbsolutePath().toString());
            workbook.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dirName + "/" + fileName;
    }


    public static <E> void forEach(
            Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(action);

        int index = 0;
        for (E element : elements) {
            action.accept(index++, element);
        }
    }
}
