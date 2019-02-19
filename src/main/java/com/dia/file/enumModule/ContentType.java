package com.dia.file.enumModule;

/**
 * 开发公司：青岛上朝信息科技有限公司
 * 版权：青岛上朝信息科技有限公司
 * <p>
 * 类功能描述
 *
 * @author 刘志强
 * @created 2018/5/22.
 */
public enum ContentType {
    doc("application/msword","./file/word/"),
    docx("application/msword","./file/word/"),
    xls("application/vnd.ms-excel","./file/excel/"),
    xlsx("application/vnd.ms-excel","./file/excel/"),
    jpg("image/jpeg","./file/image/"),
    png("image/jpeg","./file/image/"),
    pdf("application/pdf","./file/pdf/");

    private String type;
    private String path;

    public static ContentType ContentTypeStr(String string){
        if(string!=null){
            try{
                return Enum.valueOf(ContentType.class, string.trim());
            }
            catch(IllegalArgumentException ex){
            }
        }
               return null;
    }


    ContentType(String type, String path) {
        this.path = path;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
