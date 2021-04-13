package org.moonlight.office2pdf.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 转换需要用到的一些配置信息
 * @author Moonlight
 * @date 2021/4/10 10:27
 */
@Data
@Accessors(chain = true)
public class ConvertConfig {

    /** 转换出来的PDF文件的存放路径 **/
    private String convertFilePath;

    /** 是否上传转换出来的文件 **/
    private Boolean uploadConvertFile;

    /** 是否在上传后删除转换出来的文件 **/
    private Boolean delConvertFileAfterUpload;

    /** 转换出来的PDF文件的上传路径 **/
    private String convertFileUploadPath;

    /** ftp-IP **/
    private String ftpIp;

    /** ftp-端口 **/
    private Integer ftpPort;

    /** ftp-登录用户名 **/
    private String ftpUserName;

    /** ftp-登录密码 **/
    private String ftpPassword;
}
