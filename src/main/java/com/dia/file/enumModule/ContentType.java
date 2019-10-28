package com.dia.file.enumModule;

/**
 * 开发公司：xxx公司
 * 版权：xxx公司
 * <p>
 * 类功能描述
 *
 * @author 刘志强
 * @created 2018/5/22.
 */
public enum ContentType {
    doc("application/msword"),
    docx("application/msword"),
    xls("application/vnd.ms-excel"),
    xlsx("application/vnd.ms-excel"),
    jpg("image/jpeg"),
    png("image/jpeg"),
    gif("image/gif"),
    qt("text/plain"),
    pdf("application/pdf");

    private String type;

    public static ContentType ContentTypeStr(String string){
        if(string!=null){
            try{
                return Enum.valueOf(ContentType.class, string.trim());
            }
            catch(IllegalArgumentException ex){
            }
        }
        return Enum.valueOf(ContentType.class, "qt".trim());
    }


    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
