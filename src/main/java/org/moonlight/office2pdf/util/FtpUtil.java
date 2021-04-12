package org.moonlight.office2pdf.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;

public class FtpUtil {

    private static FTPClient ftpClient;

    /**
     * 连接Ftp
     */
    public static boolean connectFtp(String url, int port, String username, String password) {
        ftpClient = new FTPClient();
        int reply;
        try {
            ftpClient.connect(url, port);
            ftpClient.login(username, password);
            // 设置文件类型为二进制文件
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 设置被动模式
            ftpClient.enterLocalPassiveMode();
            ftpClient.setKeepAlive(true);
            // 获取返回码
            reply = ftpClient.getReplyCode();
            // 返回码是否合法
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 关闭FTP连接
     */
    public static void closeFtp() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过文件路径和文件名从FTP下载文件并以流的形式返回
     *
     * @param url
     * @param port
     * @param username
     * @param password
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return
     */
    public static InputStream download2InputStream(String url, int port, String username, String password, String filePath, String fileName) {
        InputStream inputStream = null;
        if (connectFtp(url, port, username, password)) {
            try {
                String path = filePath.substring(0, filePath.lastIndexOf("/"));
                // 被动模式
                ftpClient.enterLocalPassiveMode();
                // 更改工作目录
                ftpClient.changeWorkingDirectory(new String(path.getBytes("utf-8"),"ISO-8859-1"));
                // 获取文件流
                inputStream = ftpClient.retrieveFileStream(new String(fileName.getBytes("utf-8"),"ISO-8859-1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 通过文件路径从FTP下载文件并以流的形式返回
     *
     * @param url
     * @param port
     * @param username
     * @param password
     * @param filePath 文件路径
     * @return
     */
    public static InputStream download2InputStream(String url, int port, String username, String password, String filePath) {
        InputStream inputStream = null;
        if (connectFtp(url, port, username, password)) {
            try {
                // 被动模式
                ftpClient.enterLocalPassiveMode();
                // 获取文件流
                inputStream = ftpClient.retrieveFileStream(new String(filePath.getBytes("utf-8"),"ISO-8859-1"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 从FTP下载文件并以file的形式返回
     *
     * @param url
     * @param port
     * @param username
     * @param password
     * @param filePath     文件路径
     * @param fileSavePath 文件名
     * @return
     */
    public static File download2File(String url, int port, String username, String password, String filePath, String fileSavePath) {
        File tempFile = null;
        FileOutputStream outputStream = null;
        if (connectFtp(url, port, username, password)) {
            try {
                String path = filePath.substring(0, filePath.lastIndexOf("/"));
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                // 被动模式
                ftpClient.enterLocalPassiveMode();
                // 更改工作目录
                ftpClient.changeWorkingDirectory(new String(path.getBytes("utf-8"),"ISO-8859-1"));
                //
                tempFile = new File(fileSavePath + "/" + fileName);
                if (!tempFile.exists()) {
                    if (!tempFile.getParentFile().exists()) {
                        tempFile.getParentFile().mkdirs();
                    }
                    tempFile.createNewFile();
                }
                outputStream = new FileOutputStream(tempFile);

                boolean retrieveFile = false;
                do {
                    retrieveFile = ftpClient.retrieveFile(new String(fileName.getBytes("utf-8"),"ISO-8859-1"), outputStream);
                } while (!retrieveFile);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return tempFile;
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url, int port, String username, String password, String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        int retryCount = 5;
        while (retryCount-- > 0) {
            try {
                int reply;
                // 连接FTP服务器
                ftp.connect(url, port);
                ////登录
                ftp.login(username, password);
                reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                    return success;
                }
                // 被动模式
                ftp.enterLocalPassiveMode();
                //回到根目录
                ftp.changeWorkingDirectory("/");

                if (createDir(ftp, path)) {
                    // 切换到新建目录
                    ftp.changeWorkingDirectory(new String(path.getBytes("utf-8"),"ISO-8859-1"));
                    // 用字节流，防止数据丢失
                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                    ftp.storeFile(new String(filename.getBytes("utf-8"),"ISO-8859-1"), input);

                    input.close();
                    ftp.logout();
                    success = true;
                    retryCount = 0;
                }
            } catch (ConnectException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } finally {
                if (ftp.isConnected()) {
                    try {
                        ftp.disconnect();
                    } catch (IOException ioe) {
                    }
                }
            }
        }
        return success;
    }

    /**
     * 查找指定目录是否存在  不存在创建目录
     *
     * @param ftpClient 要检查的FTP服务器
     * @param filePath  要查找的目录
     * @return boolean:存在:true，不存在:false
     * @throws IOException
     */
    public static boolean createDir(FTPClient ftpClient, String filePath) throws IOException {
        boolean existFlag = false;
        filePath = new String(filePath.getBytes("utf-8"),"ISO-8859-1");
        try {
            int index;
            while ((index = filePath.indexOf("/")) != -1) {
                if (!ftpClient.changeWorkingDirectory(filePath.substring(0, index))) {
                    ftpClient.makeDirectory(filePath.substring(0, index));
                }
                ftpClient.changeWorkingDirectory(filePath.substring(0, index));
                filePath = filePath.substring(index + 1, filePath.length());
            }
            if (!"".equals(filePath)) {
                if (!ftpClient.changeWorkingDirectory(filePath)) {
                    ftpClient.makeDirectory(filePath);
                }
            }
            existFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existFlag;
    }

    /**
     * 检验指定路径的文件是否存在ftp服务器中
     *
     * @param filePath 文件路径
     * @param user 登陆用户名
     * @param passward 登陆密码
     * @param ip IP地址
     * @param port 端口号
     * @return
     */
    public static boolean isFTPFileExist(String filePath, String user, String passward, String ip, int port) {
        FTPClient ftp = new FTPClient();
        try {
            // 连接ftp服务器
            ftp.connect(ip, port);
            // 登陆
            ftp.login(user, passward);
            // 检验登陆操作的返回码是否正确
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.disconnect();
                return false;
            }
            // 被动模式
            ftp.enterLocalPassiveMode();
            // 设置文件类型为二进制
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // 提取绝对地址的目录以及文件名
            String path = filePath.substring(0, filePath.lastIndexOf("/"));
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());

            // 进入文件所在目录，注意编码格式，以能够正确识别中文目录
            ftp.changeWorkingDirectory(new String(path.getBytes("utf-8"),"ISO-8859-1"));

            // 检验文件是否存在
            InputStream is = ftp.retrieveFileStream(new String(fileName.getBytes("utf-8"),"ISO-8859-1"));
            if (is == null || ftp.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                return false;
            }

            is.close();
            ftp.completePendingCommand();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp != null && ftp.isConnected()) {
                try {
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
