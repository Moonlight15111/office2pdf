package org.moonlight.office2pdf.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/4/12 15:25
 */
public class DocumentProcessUtil {

    public static void main(String[] args) {
//        System.out.println(DocumentProcessUtil.processDocumentByFilePath("D:/Moonlight/testPdf.pdf"));
//        InputStream in = FTPUtil.download2InputStream("192.168.1.53",21,"admin","123456","/test/pdf/justTestWord.pdf","justTestWord.pdf");
//        System.out.println(DocumentProcessUtil.processDocumentByInputstream(in,"justTestWord.pdf"));
        String ss = "服务时间\n" +
                "合同签订之日起 6 个月内完成系统开发、调试、验收。项目验收合格后，\n" +
                "中标供应商为采购人提供配套的运行支撑环境，采用租赁服务的方式，周期为 1 年。\n" +
                "五、 服务地点： 甲方指定地点";
//        Pattern pattern = Pattern.compile("(?<=（大写：人民币【)[^（大写：人民币【]*(?=】.）)");
        ss = ss.replaceAll("\\n|\\r","");
//        String patternStr = "总费用为.*?（大写：人民币【(?<money>.*?)】）   ";
        Pattern pattern = Pattern.compile("服务时间合同签订之日起 6 个月内完成系统开发、调试、验收。项目验收合格");
//        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(ss);
//        if (matcher.find()){
//            System.out.println( matcher.group("money"));
//        }
        System.out.println(matcher.find());
        matcher = pattern.matcher(ss);
        int count = 0;
        while (matcher.find()) {
            System.out.println(" this is count === " + count++);
            System.out.println(matcher.group("frontContractSumCh"));
//            if (matcher.group("backContractSum").contains("￥") || matcher.group("backContractSum").contains(",")) {
//                System.out.println(matcher.group("backContractSum").replaceAll("￥|,",""));
//            }
        }
    }

    /**
     * 根据文件路径解析文档
     * @param filePath
     * @return
     */
    public static void processDocumentByFilePath (String filePath) {
        if (!StringUtils.isNotBlank(filePath)) {
            return;
        }
        String context = "";
        FileInputStream fis = null;
        // 获取文件后缀名
        int lastIndex = -1;
        String subFix = "";
        if ((filePath != null && filePath.length() > 0) && (lastIndex = filePath.lastIndexOf(".")) > -1) {
            subFix = filePath.substring(lastIndex + 1);
        }
        try{
            fis = new FileInputStream(new File(filePath));
            // 判断文件类型
            switch (subFix){
                // word文档 97-2003
                case "doc":
                    context = getContextFromWord97(fis,context);
                    break;
                // word文档 >= 2007
                case "docx":
                    context = getContextFromWord07(fis,context);
                    break;
                // pdf文档
                case "pdf":
                    context = getContextFromPDF(fis,context);
                    break;
                default:
                    System.out.println("请传入pdf文档或word文档");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (fis != null) {
                    fis.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件流和文件名进行解析文档内容
     * @param inputStream 文件流
     * @param fileName
     * @return
     */
    public static void processDocumentByInputstream (InputStream inputStream, String fileName) {
        if (inputStream == null) {
            return;
        }
        String context = "";
        try{
            // 获取文件后缀名
            int lastIndex = -1;
            String subFix = "";
            if ((fileName != null && fileName.length() > 0) && (lastIndex = fileName.lastIndexOf(".")) > -1) {
                subFix = fileName.substring(lastIndex + 1);
            }
            // 判断文件类型
            switch (subFix){
                // word文档 97-2003
                case "doc":
                    context = getContextFromWord97(inputStream,context);
                    break;
                // word文档 >= 2007
                case "docx":
                    context = getContextFromWord07(inputStream,context);
                    break;
                // pdf文档
                case "pdf":
                    context = getContextFromPDF(inputStream,context);
                    break;
                default:
                    System.err.println("请传入pdf文档或word文档");
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (inputStream != null) {
                    inputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取word文档(97-2003)内容
     * @param fis
     * @return
     */
    public static String getContextFromWord97 (InputStream fis,String context) {
        WordExtractor we = null;
        try{
            we = new WordExtractor(fis);
            context = we.getText();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (we != null) {
                    we.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return context;
    }

    /**
     * 获取word文档(2007)内容
     * @param fis
     * @return
     */
    public static String getContextFromWord07 (InputStream fis,String context) {
        XWPFDocument docx = null;
        XWPFWordExtractor we = null;
        try{
            docx = new XWPFDocument(fis);
            we = new XWPFWordExtractor(docx);
            context = we.getText();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (we != null) {
                    we.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return context;
    }

    /**
     * 获取PDF文档内容
     * @param fis
     * @return
     */
    public static String getContextFromPDF (InputStream fis,String context) {
        PDDocument document = null;
        try{
            //加载 pdf 文档
            PDFParser parser = new PDFParser(new RandomAccessBuffer(fis));
            parser.parse();
            document = parser.getPDDocument();
            // 获取页码
            int pages = document.getNumberOfPages();
            // 设置 文本剥离器 按顺序输出
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            context = stripper.getText(document);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (document != null) {
                    document.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return context;
    }

}
