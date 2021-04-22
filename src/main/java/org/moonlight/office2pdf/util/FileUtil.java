package org.moonlight.office2pdf.util;

import info.monitorenter.cpdetector.io.*;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.office2pdf.common.Const;

import java.io.*;
import java.nio.charset.Charset;;

/**
 * file util
 * @author Moonlight
 * @date 2021/4/10 11:18
 */
public class FileUtil {

    /**
     * 获取文件后缀名
     * @param path 文件路径
     * @return 文件后缀名
     */
    public static String getFileSuffix(String path) {
        int lastIndex;
        if (StringUtils.isNotBlank(path) && (lastIndex = path.lastIndexOf(Const.DOT)) > -1) {
            return path.substring(lastIndex + 1);
        }
        return null;
    }

    public static boolean createFile(File file) throws IOException {
        if (!file.exists()) {
            boolean mkdirs = true;
            if (!file.getParentFile().exists()) {
                mkdirs = file.getParentFile().mkdirs();
            }
            if (!mkdirs) {
                return false;
            }
            return file.createNewFile();
        }
        return true;
    }

    public static void delFile(String waterMarkImgPath) {
        if (StringUtils.isBlank(waterMarkImgPath)) {
            return;
        }
        File file = new File(waterMarkImgPath);
        if (!file.exists()) {
            return;
        }
        file.delete();
    }

    public static void saveFile2Utf8(File targetFile, File oriFile, String oriEncoding) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(oriFile), oriEncoding);
             OutputStreamWriter outWrite = new OutputStreamWriter(new FileOutputStream(targetFile), "utf-8")) {

            int r;
            while ((r = inputStreamReader.read()) != -1) {
                outWrite.write(r);
            }
        }
    }

    /**
     * 利用第三方开源包cpdetector获取文件编码格式.
     *   1、cpDetector内置了一些常用的探测实现类,这些探测实现类的实例可以通过add方法加进来,
     *      如:ParsingDetector、 JChardetFacade、ASCIIDetector、UnicodeDetector.
     *   2、detector按照“谁最先返回非空的探测结果,就以该结果为准”的原则.
     *   3、cpDetector是基于统计学原理的,不保证完全正确.
     * @return 返回文件编码类型：GBK、UTF-8、UTF-16BE、ISO_8859_1
     */
    public static String getFileCharset(File file) throws IOException {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
		/*ParsingDetector可用于检查HTML、XML等文件或字符流的编码,
		 * 构造方法中的参数用于指示是否显示探测过程的详细信息，为false不显示。
	    */
        detector.add(new ParsingDetector(false));
		/*JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码测定。
		 * 所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以再多加几个探测器，
		 * 比如下面的ASCIIDetector、UnicodeDetector等。
        */
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            charset = detector.detectCodepage(is, 128);
        }

        String charsetName = "GBK";
        if (charset != null) {
            if ("US-ASCII".equals(charset.name())) {
                charsetName = "ISO_8859_1";
            } else if (charset.name().startsWith("UTF")) {
                charsetName = charset.name();
            }
        }
        return charsetName;
    }

    public static String getCharset(File file) throws IOException {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            boolean checked = false;
            bis.mark(100);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                bis.close();
                return charset;
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 > read || read > 0xBF) {
                            break;
                        }
                    } else if (0xE0 <= read) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            bis.close();
        }
        return charset;
    }
}
