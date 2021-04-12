package org.moonlight.office2pdf.connect;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * open office 连接服务相关配置
 * @author Moonlight
 * @date 2021/4/10 11:44
 */
@Data
@Accessors(chain = true)
public class OpenOfficeConnectionConfig {

    /** openOffice服务的host, 默认本机 **/
    private String openOfficeHost = "127.0.0.1";

    /** openOffice服务的端口, 默认8100 **/
    private Integer openOfficePort = 8100;

    /** openOffice的路径，如: C:/Program Files (x86)/OpenOffice 4/program/soffice.exe **/
    private String openOfficePath = "C:/Program Files (x86)/OpenOffice 4/program/soffice.exe";

    /** 连接的数量 **/
    private Integer connectionSize = 2;

}