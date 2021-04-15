package org.moonlight.office2pdf.convert;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.office2pdf.common.Const;
import org.moonlight.office2pdf.config.ConvertConfig;
import org.moonlight.office2pdf.connect.OpenOfficeConnectionConfig;
import org.moonlight.office2pdf.connect.OpenOfficeConnectionManager;
import org.moonlight.office2pdf.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * office文件转成PDF
 * @author Moonlight
 * @date 2021/4/10 10:25
 */
public class Office2PdfConvert {

    public static void main(String[] args) {
        String testExcelPath = "E:\\Moonlight\\测试文件\\ict测试\\justtestEXcel.xlsx";
        String testWordPath = "E:\\Moonlight\\测试文件\\ict测试\\justTestWord.docx";
        String testPPTPath = "E:\\Moonlight\\测试文件\\ict测试\\justTestPPT.pptx";

        try {
            OpenOfficeConnectionManager.init(new OpenOfficeConnectionConfig());

            Office2PdfConvert convert = new Office2PdfConvert(
                    new ConvertConfig().setConvertFilePath("E:\\Moonlight\\测试文件\\ict测试")
            );

            System.out.println("convert excel = " + convert.office2Pdf(testExcelPath));
            System.out.println("convert word = " + convert.office2Pdf(testWordPath, " water mark test "));
            System.out.println("convert ppt = " + convert.office2Pdf(testPPTPath, " test convert ppt "));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ConvertConfig convertConfig;

    public Office2PdfConvert(ConvertConfig convertConfig) {
       this.convertConfig = convertConfig;
    }

    private void configPreValidate() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(convertConfig.getConvertFilePath())) {
            builder.append("转换文件的存储路径不能为空;");
        }
        if (convertConfig.getUploadConvertFile() != null
                && convertConfig.getUploadConvertFile()
                && StringUtils.isAnyBlank(convertConfig.getFtpIp(), String.valueOf(convertConfig.getFtpPort()),
                convertConfig.getFtpUserName(), convertConfig.getFtpPassword(), convertConfig.getConvertFileUploadPath())) {
            builder.append("如果文件需要上传至FTP, 请配置好FTP相关的配置;");
        }
        if (builder.length() > 0) {
            throw new RuntimeException(builder.deleteCharAt(builder.length() - 1).toString());
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * office文件转为PDF
     * @param originalFilePath 源文件的绝对路径
     * @return String 转换出来的PDF文件的存储路径
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 11:16
     */
    public String office2Pdf(String originalFilePath) throws IOException, DocumentException, InterruptedException {
        if (StringUtils.isBlank(originalFilePath)) {
            throw new RuntimeException("参数错误, 请传入正确的文件路径");
        }
        return office2Pdf(new File(originalFilePath), null);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * office文件转为PDF并设置水印
     * @param originalFilePath 源文件的绝对路径
     * @return String 转换出来的PDF文件的存储路径
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 11:16
     */
    public String office2Pdf(String originalFilePath, String waterMarkContent) throws IOException, DocumentException, InterruptedException {
        if (StringUtils.isBlank(originalFilePath)) {
            throw new RuntimeException("参数错误, 请传入正确的文件路径");
        }
        return office2Pdf(new File(originalFilePath), waterMarkContent);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * office文件转pdf文件并设置水印
     * @param originalFile 源文件对象
     * @param watermarkContent 水印内容
     * @return String 转换出来的PDF文件的存储路径
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 11:17
     */
    public String office2Pdf(File originalFile, String watermarkContent) throws IOException, DocumentException, InterruptedException {
        if (originalFile == null || !originalFile.exists()) {
            throw new RuntimeException("文件不存在,请确认文件是否存在或路径是否正确");
        }
        configPreValidate();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String convertFileName = originalFile.getName() + simpleDateFormat.format(new Date()) + Const.PDF_FILE_NAME_SUFFIX_WITH_DOT;
        String convertFilePath = convertConfig.getConvertFilePath() + File.separator + convertFileName;
        File convertFile = null;

        OpenOfficeConnection connection = null;
//        Process p = null;
        try {
            connection = OpenOfficeConnectionManager.getOpenOfficeConnection();
            convertFile = new File(convertFilePath);
            if (!FileUtil.createFile(convertFile)) {
                throw new RuntimeException("创建转换文件失败");
            }
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(originalFile, convertFile);
            // 调openOffice转换
            // 首先需要安装OpenOffice 下载地址https://www.openoffice.org/download/
            // 其次需要 com.artofsolving.jodconverter 2.2.2 版本 其他版本不支持docx、xlsx等等且该版本不存在maven中央仓库中 下载地址 https://sourceforge.net/projects/jodconverter/files/latest/download?source=files
//            String command = "C:/Program Files (x86)/OpenOffice 4/program/soffice.exe -headless -accept=\"socket,host="
//                    + "127.0.0.1,port=8100;urp;\"";
//            p = Runtime.getRuntime().exec(command);
            // 连接openOffice 如果无法连接建议先手动启动 soffice.exe 或者等待一下再重试 猜测是因为第一次连接需要将服务打开, 这可能要点时间
//            connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
//            connection.connect();
            // 转换
//            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
//            converter.convert(originalFile, convertFile);
            // 关闭连接
//            connection.disconnect();
//            p.destroy();

            if (StringUtils.isNotBlank(watermarkContent)) {
                return pdfSetWaterMark(convertFile, watermarkContent);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
//                OpenOfficeConnectionManager.closeConnection();
            }
//            if (p != null) {
//                p.destroy();
//            }
        }
        return convertFilePath;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 为pdf文件设置水印
     *
     * 这个方法是这样的:
     *     1.先读取源pdf文件的数据
     *     2.在读取到的数据上面操作
     *     3.把最终数据写到另一个pdf文件中
     *
     * @param pdfFile 源pdf文件
     * @param waterMarkContent 水印内容
     * @return String 设置了水印的pdf文件的存储路径
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 11:24
     */
    public String pdfSetWaterMark(File pdfFile, String waterMarkContent) throws IOException, DocumentException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new RuntimeException("文件不存在,请确认文件是否存在或路径是否正确");
        }

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String finalFileName = pdfFile.getName() + simpleDateFormat.format(new Date()) + "_WaterMark" + Const.PDF_FILE_NAME_SUFFIX_WITH_DOT;;
        String finalFilePath = convertConfig.getConvertFilePath() + File.separator + finalFileName;
        File finalFile = null;

        finalFile = new File(finalFilePath);
        if (!FileUtil.createFile(finalFile)) {
            throw new RuntimeException("创建水印文件失败");
        }

        doPdfSetWaterMark(pdfFile.getAbsolutePath(), finalFile, waterMarkContent);

        return finalFilePath;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 给pdf文件加水印 - 源文件路径
     * @param oriPdfFilePath 源pdf文件绝对路径
     * @param finalFile 加水印后的最终文件
     * @param waterMarkContent 水印内容
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 12:27
     */
    private void doPdfSetWaterMark(String oriPdfFilePath, File finalFile, String waterMarkContent) throws IOException, DocumentException {
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(oriPdfFilePath);
            doSetWaterMark(pdfReader, finalFile, createWaterMarkImg(waterMarkContent));
        } finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 给pdf文件加水印 - 源文件输入流
     * @param oriPdfInputStream 源pdf文件输入流
     * @param finalFile 加水印后的最终文件
     * @param waterMarkContent 水印内容
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 12:26
     */
    private void doPdfSetWaterMark(InputStream oriPdfInputStream, File finalFile, String waterMarkContent) throws IOException, DocumentException {
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(oriPdfInputStream);
            doSetWaterMark(pdfReader, finalFile, createWaterMarkImg(waterMarkContent));
        } finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 根据传入的水印内容生成一张水印图片
     * @param waterMarkContent 水印内容
     * @return String 水印图片的存储路径
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 11:59
     */
    private String createWaterMarkImg(String waterMarkContent) throws IOException {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String waterMarkImgPath = convertConfig.getConvertFilePath() + File.separator
                + "markImage" + simpleDateFormat.format(new Date()) + ".png";

        File imgFile = new File(waterMarkImgPath);
        FileUtil.createFile(imgFile);

        // 水印图片 Note: 使用图片水印是因为前端没办法显示文字水印
        BufferedImage buffImg = new BufferedImage(1300,100,BufferedImage.TYPE_INT_RGB);
        Graphics2D graph = buffImg.createGraphics();
        buffImg = graph.getDeviceConfiguration().createCompatibleImage(1300, 100, Transparency.TRANSLUCENT);
        graph = buffImg.createGraphics();
        graph.setFont(new Font("微软雅黑", Font.BOLD,60));
        graph.setColor(Color.lightGray);
        graph.drawString(waterMarkContent,5,50);
        ImageIO.write(buffImg, "PNG", imgFile);
        graph.dispose();

        return waterMarkImgPath;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * pdf文件加水印
     * @param pdfReader Pdf操作对象
     * @param finalFile 加水印的文件
     * @param waterMarkImgPath 水印图片的路径
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/4/12 12:21
     */
    private void doSetWaterMark(PdfReader pdfReader, File finalFile, String waterMarkImgPath) throws IOException, DocumentException {
        PdfStamper stamper = null;
        PdfContentByte content = null;
        Rectangle rectangle = null;
        try {
            stamper = new PdfStamper(pdfReader, new FileOutputStream(finalFile));

            int pageTotal = pdfReader.getNumberOfPages() + 1;
            float width, height;
            com.itextpdf.text.Image waterMarkImg = com.itextpdf.text.Image.getInstance(waterMarkImgPath);
            for (int i = 1; i < pageTotal; i++) {
                rectangle = pdfReader.getPageSize(i);
                width = rectangle.getWidth();
                height = rectangle.getHeight();

                content = stamper.getOverContent(i);

                content.beginText();

                // 左上角
                waterMarkImg.setAbsolutePosition(50, height - 150);
                waterMarkImg.scaleToFit(200, 200);
                waterMarkImg.setRotationDegrees(45);
                content.addImage(waterMarkImg);
                // 右上角
                waterMarkImg.setAbsolutePosition(width - 180, height - 150);
                waterMarkImg.scaleToFit(200, 200);
                waterMarkImg.setRotationDegrees(135);
                content.addImage(waterMarkImg);
                // 中间
                waterMarkImg.setAbsolutePosition(width / 2 - 150, height / 2);
                waterMarkImg.scaleToFit(200, 200);
                content.addImage(waterMarkImg);
                // 左下角
                waterMarkImg.setAbsolutePosition(50, 50);
                waterMarkImg.scaleToFit(200, 200);
                waterMarkImg.setRotationDegrees(135);
                content.addImage(waterMarkImg);
                // 右下角
                waterMarkImg.setAbsolutePosition(width - 150, 50);
                waterMarkImg.scaleToFit(200, 200);
                waterMarkImg.setRotationDegrees(45);
                content.addImage(waterMarkImg);

                content.endText();
            /* 文字水印 前端显示的时候有点问题 */
//                // 颜色
//                content.setColorFill(BaseColor.GRAY);
//                // 字体、大小
//                content.setFontAndSize(base, 20);
//                // 起始位置
//                content.setTextMatrix(250, 250);
//                content.showTextAligned(Element.ALIGN_CENTER,waterMarkText,250, 250, 10);
//                content.setColorFill(BaseColor.BLACK);
//                content.setFontAndSize(base, 8);
//                content.showTextAligned(Element.ALIGN_CENTER, waterMarkText, 300, 10, 0);
            }
        } finally {
            if (stamper != null){
                stamper.flush();
                stamper.close();
            }
            if (StringUtils.isNotBlank(waterMarkImgPath)) {
                FileUtil.delFile(waterMarkImgPath);
            }
        }
    }

}
