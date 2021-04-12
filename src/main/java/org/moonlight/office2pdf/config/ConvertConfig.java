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

    /** openOffice服务的host, 默认本机 **/
    private String openOfficeHost = "127.0.0.1";

    /** openOffice服务的端口, 默认8100 **/
    private Integer openOfficePort = 8100;

    /** openOffice的路径，如: C:/Program Files (x86)/OpenOffice 4/program/soffice.exe **/
    private String openOfficePath = "C:/Program Files (x86)/OpenOffice 4/program/soffice.exe";

}
