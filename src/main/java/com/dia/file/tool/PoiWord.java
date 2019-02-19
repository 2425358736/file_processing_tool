package com.dia.file.tool;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 开发公司：青岛上朝信息科技有限公司
 * 版权：青岛上朝信息科技有限公司
 * <p>
 * 类功能描述
 *
 * @author 刘志强
 * @created 2018/5/2.
 */
public class PoiWord {

    public static ResponseEntity<byte[]> wordReplace(String filePath, Map<String, Object> map, String fileName) throws IOException {
        byte[] bytes = poiTransformation(filePath,map);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/msword"));
        String [] nameArr = filePath.split("\\.");
        String type = nameArr[nameArr.length-1];
        headers.setContentDispositionFormData("attachment", new String((fileName +"."+type).getBytes("utf-8"), "ISO8859-1"));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
        return responseEntity;
    }


    public static byte[] poiTransformation(String inPath, Map<String, Object> map) throws IOException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dirName = format.format(new Date());
        String [] nameArr = inPath.split("\\.");
        String imgName = System.currentTimeMillis() + "."+nameArr[nameArr.length-1];
        InputStream is = new FileInputStream(inPath);
        XWPFDocument document;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            document = new XWPFDocument(OPCPackage.open(is));
            // 替换段落里面的变量
            replaceInPara(document, map);
            replaceInTable(document, map);
            document.write(baos);
            close(baos);
            close(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }


    public static Pattern NUMBER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
    /**
     * 替换段落里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    public static void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            PoiWord.replaceInPara(para, params);
        }
    }

    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    public static void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;
        Matcher matcher;
        if (PoiWord.matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();
            for (int i=0; i<runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String runText = run.toString();
                matcher = PoiWord.matcher(runText);
                if (matcher.find()) {
                    while ((matcher = PoiWord.matcher(runText)).find()) {
                        runText = matcher.replaceFirst(String.valueOf(params.get(matcher.group(1))));
                    }
                    //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                    //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                    para.removeRun(i);
                    para.insertNewRun(i).setText(runText);
                }
            }
        }
    }

    /**
     * 替换表格里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    public static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        PoiWord.replaceInPara(para, params);
                    }
                }
            }
        }
    }

    /**
     * 正则匹配字符串
     * @param str
     * @return
     */
    public static Matcher matcher(String str) {
        Matcher matcher = NUMBER_PATTERN.matcher(str);
        return matcher;
    }

    /**
     * 关闭输入流
     * @param is
     */
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 取出类的属性名
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    //返回对象的属性名，属性类别，属性值
    public static Map<String,Object> getFiledsInfo(Object o){
        List<Field> fieldList = new ArrayList<Field>() ;
        Class tempClass = o.getClass();
        if (StringUtils.equals(tempClass.toString(), "class java.util.HashMap")) {
            return (Map) o;
        }
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            //得到父类,然后赋给自己
            tempClass = tempClass.getSuperclass();
        }
        Map infoMap=new HashMap();
        for(int i=0;i<fieldList.size();i++){
            Object value = getFieldValueByName(fieldList.get(i).getName(), o);
            if (StringUtils.equals(fieldList.get(i).getType().toString(),"class java.util.Date")){
                if (value != null && !StringUtils.equals(value.toString(),"")) {
                    value = formatDate((Date) value, null);
                }
            }
            infoMap.put(fieldList.get(i).getName(), value != null ? value : "暂无此项数据" );
        }
        return infoMap;
    }

    public static String formatDate(Date date, String pattern) {
        String formatDate = null;
        if (pattern != null) {
            formatDate = DateFormatUtils.format(date, pattern);
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    //返回对象的属性名，属性类别，属性值
    public static String getFiledsInfoString(Object o){
        List<Field> fieldList = new ArrayList<Field>() ;
        Class tempClass = o.getClass();
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            //得到父类,然后赋给自己
            tempClass = tempClass.getSuperclass();
        }
        Map infoMap=new HashMap();
        for(int i=0;i<fieldList.size();i++){
            Object value = getFieldValueByName(fieldList.get(i).getName(), o);
            if (StringUtils.equals(fieldList.get(i).getType().toString(),"class java.util.Date")){
                if (value != null && !StringUtils.equals(value.toString(),"")) {
                    value = formatDate((Date) value, null);
                }
            }
            if (value != null) {
                infoMap.put(fieldList.get(i).getName(), value);
            }
        }
        JSONObject json = JSONObject.fromObject(infoMap);
        return json.toString();
    }
}
