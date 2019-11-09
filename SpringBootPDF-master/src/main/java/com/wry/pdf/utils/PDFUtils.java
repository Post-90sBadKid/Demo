package com.wry.pdf.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.wry.pdf.dto.PDFData;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.*;

public class PDFUtils {
    //设置生成的PDF表格的宽度
    private static int totalWidth = 520;
    // 不保存待定的更改。
    public static final int wdDoNotSaveChanges = 0;
    // PDF 格式
    public static final int wdFormatPDF = 17;

    // 基本字体和样式
    public static BaseFont bfChinese;
    public static Font fontChinese;
    // 设置字体大小
    public static Font headFont;
    public static Font keyFont;
    public static Font infoFont;
    public static Font textFont;
    public static Font spaceFont;

    static {
        try {
            // 使用iTextAsian.jar中的字体
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            // 基础字体
            fontChinese = new Font(bfChinese, 14, Font.NORMAL);
            // 设置字体大小
            headFont = new Font(bfChinese, 22, Font.BOLD);
            keyFont = new Font(bfChinese, 16, Font.BOLD);
            infoFont = new Font(bfChinese, 14, Font.BOLD);
            textFont = new Font(bfChinese, 10, Font.NORMAL);
            spaceFont = new Font(bfChinese, 3, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 无参构造方法
     */
    public PDFUtils() {
    }


    public static void exportExcel(HttpServletResponse response, String fileName, PDFData data) {
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            //调用导出PDF的方法
            exportPDF(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出PDF
     *
     * @param data
     * @param outputStream
     */
    private static void exportPDF(PDFData data, ServletOutputStream outputStream) {
        Document document = new Document();
        document.setPageSize(PageSize.A4);// 设置页面大小
        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
//            writer.setBoxSize("art", PageSize.A4);
            //设置页码
            HeaderAndFooter footer = new HeaderAndFooter();
            writer.setPageEvent(footer);
            //打开文档(document)对象
            document.open();
            //写PDF
            writePDF(document, data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭文档(document)对象
            document.close();
        }
    }

    /**
     * 根据需求封装PDF数据
     *
     * @param document
     * @param data
     * @throws Exception
     */
    private static void writePDF(Document document, PDFData data) throws Exception {
        //标题
        List<String> thead = data.getThead();
        //添加标题
        addTitle(document, data.getTitle(), headFont);

        PdfPTable table = createTable(thead.size());
        //设置pdf  Table的标题
        for (String column : thead) {
            PdfPCell cell = createCell(column, keyFont, Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        //内容
        List<List<String>> tbody = data.getTbody();
        for (List<String> stringList : tbody) {
            for (String s : stringList) {
                PdfPCell cell = createCell(s, textFont);
                table.addCell(cell);
            }
        }
        document.add(table);
    }


    /**
     * 添加标题
     *  
     *
     * @param document
     * @param title
     * @param font
     * @throws DocumentException
     */
    public static void addTitle(Document document, String title, Font font) throws DocumentException {
        Paragraph t = new Paragraph(title, font);
        // 居中显示
        t.setAlignment(1);
        //设置标题与正文段落间距
        t.setSpacingAfter(10);
        document.add(t);

    }

    /**
     * 添加小标题
     *  
     *
     * @param document
     * @param title
     * @param font
     * @throws DocumentException
     */
    public static void addSerTitle(Document document, String title, Font font) throws DocumentException {
        document.add(new Paragraph(title, font));

    }

    /**
     * 添加内容
     *
     * @param document
     * @param content
     * @param font
     * @throws DocumentException
     */
    public static void addDocument(Document document, String content, Font font) throws DocumentException {
        document.add(new Paragraph(content, font));
    }

    /**
     * 添加段落
     *  
     *
     * @param document
     * @param content
     * @param font
     */
    public static void addParagraph(Document document, String content, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph(content, font);
        // 首行缩进
        paragraph.setFirstLineIndent(24f);
        // 设置段段落居中
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        // 行间距
        paragraph.setLeading(20f);
        // 设置段落前后间距
        paragraph.setSpacingBefore(5f);
        paragraph.setSpacingAfter(10f);
        // 设置段落连续
        paragraph.setKeepTogether(true);
        document.add(paragraph);
    }


    /**
     * 创建PDF表格 (一)
     *  
     *
     * @param colNumber
     * @return
     */
    public static PdfPTable createTable(int colNumber) {
        PdfPTable table = new PdfPTable(colNumber);
        try {
            table.setTotalWidth(totalWidth); // 设置表格的总宽度
            table.setLockedWidth(true); // 锁定宽度
            table.setHorizontalAlignment(Element.ALIGN_CENTER); // 对齐方式，左对齐
            table.getDefaultCell().setBorder(1); // 默认单元格的border为1
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建PDF表格 (二)
     *  
     *
     * @param widths
     * @return
     */
    public static PdfPTable createTable(float[] widths) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(totalWidth);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * 创建单元格，可调整对其的格式
     *  
     *
     * @param value
     * @param font
     * @param align
     * @return
     */
    public static PdfPCell createCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建无边框的单元格，可调整对其的格式
     *  
     *
     * @param value
     * @param font
     * @param align
     * @return
     */
    public static PdfPCell createNoBorderCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        // 设置无边框
        cell.setBorderWidth(0);
        return cell;
    }

    /**
     * 创建简单单元格
     *  
     *
     * @param value
     * @param font
     * @return
     */
    public static PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建单元格，可设置对齐格式和占用的列数
     *  
     *
     * @param value
     * @param font
     * @param align
     * @param colspan 合并列
     */
    public static PdfPCell createCell(String value, Font font, int align, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建PDF 文件的内容
     *
     * @param value     内容
     * @param font      字体
     * @param align     对齐方式
     * @param colspan   合并的列数
     * @param boderFlag 是否需要边框
     * @return
     */
    private static PdfPCell createCell(String value, Font font, int align, int colspan, boolean boderFlag) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        cell.setPadding(3.0f);
        if (!boderFlag) {
            cell.setBorder(0);
            cell.setPaddingTop(15.0f);
            cell.setPaddingBottom(8.0f);
        }
        return cell;
    }


    /**
     * 创建单元格，可设置占用的列数
     *  
     *
     * @param value
     * @param font
     * @param align
     * @param colspan            合并列
     * @param rowspan            合并行
     * @return
     */
    public static PdfPCell createCell(String value, Font font, int align, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    ;

    /**
     * 创建一个跨多行的单元格
     *  
     *
     * @param rows                 所占行数
     * @param paragraph            单元格内容文字
     * @param align                对齐方式
     */
    public static PdfPCell newPdfPCellByRows(int rows, Paragraph paragraph, int align) {
        PdfPTable iTable = new PdfPTable(1);
        PdfPCell iCell = new PdfPCell();
        iCell.setFixedHeight(iCell.getFixedHeight() * rows);
        iTable.addCell(iCell);
        iCell.addElement(paragraph);
        iCell.setHorizontalAlignment(align);
        PdfPCell cell = new PdfPCell(iTable);
        return cell;
    }

    /**
     * 创建一个跨多列的单元格
     *  
     *
     * @param colspan              所占列数
     * @param paragraph            单元格内容文字
     * @param align                对齐方式
     */
    public static PdfPCell newPdfPCellByColspan(int colspan, Paragraph paragraph, int align) {
        PdfPTable iTable = new PdfPTable(1);
        PdfPCell iCell = new PdfPCell();
        iCell.setColspan(colspan);
        iCell.setBorder(0);
        iCell.addElement(paragraph);
        iCell.setHorizontalAlignment(align);
        iTable.addCell(iCell);
        PdfPCell cell = new PdfPCell(iTable);
        return cell;
    }

    /**
     * 创建图片
     *  
     *
     * @param imgPath            图片路径
     * @param width              宽
     * @param height             高
     * @param align              对齐方式
     * @return
     */
    public static Image newImage(String imgPath, int width, int height, int align) {
        Image img = null;
        try {
            img = Image.getInstance(imgPath);
            img.scaleAbsolute(width, height);
            img.setAlignment(align);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * 读取模板生成PDF
     *  
     *
     * @param templatePDF
     * @param destFilePath
     * @param destFileName
     * @param hashMap
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static String readTempCreatePDF(String templatePDF, String destFilePath, String destFileName,
                                           Map<String, Object> hashMap) throws IOException, DocumentException {
        FileOutputStream fos = null;
        PdfReader reader = null;
        String retFile = destFilePath + File.separator + destFileName;
        try {
            File file = new File(destFilePath);
            // 判断文件夹是否存在,如果不存在则创建文件夹
            if (!file.exists()) {
                file.mkdirs();
            }
            fos = new FileOutputStream(retFile);// 设定输出PDF文件名
            reader = new PdfReader(templatePDF);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamp = new PdfStamper(reader, baos);
            AcroFields fields = stamp.getAcroFields();
            fields.addSubstitutionFont(bfChinese);// 显示中文
            fields = setField(fields, hashMap);
            stamp.setFormFlattening(true);// 模板中的变量赋值之后不能编辑
            stamp.close();
            fos.write(baos.toByteArray());
            fos.close();
            reader.close();
            baos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            fos.close();
            if (reader != null) {
                reader.close();
            }
        }
        return retFile;
    }

    /**
     * 给属性赋值
     *  
     *
     * @param fields
     * @param hashMap
     * @return
     */
    public static AcroFields setField(AcroFields fields, Map<String, Object> hashMap) {
        // map里保存要填入的数据，key和文本域的name一样
        Set<String> it = hashMap.keySet();
        Iterator<String> itr = it.iterator();
        while (itr.hasNext()) {
            try {
                Object key = itr.next();
                Object value = hashMap.get(key);
                if (value == null) {
                    fields.setField(key.toString(), "");
                } else {
                    fields.setField(key.toString(), value.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        return fields;
    }

    /**
     * 利用反射机制，获取值
     *  
     *
     * @param fieldName
     * @param t
     * @return
     */
    public static <T> String getFieldValue(String fieldName, T t) {
        String value = "";
        Method[] methods = t.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            if (methodName.substring(0, 3).toUpperCase().equals("GET")
                    && methodName.substring(3).toUpperCase().equals(fieldName.toUpperCase())) {
                Method method = methods[i];
                try {
                    value = method.invoke(t, new Object[]{}).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 根据数据bean得到PDF中要写入的textField的名字
     *  
     *
     * @param t
     * @return
     */
    public static <T> List<String> getFieldName(T t) {
        List<String> fieldNames = new ArrayList<String>();
        Field[] fields = t.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            String tmpName = fields[i].getName();
            fieldNames.add(tmpName);
        }
        return fieldNames;
    }

    /**
     * 实体类转化为map
     *  
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> objectToMap(T obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * List集合转化为map
     *  
     *
     * @param obj
     * @return
     */
    public static <T> Map<String, Object> listToMap(List<T> obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < obj.size(); i++) {
            if (obj.get(i) != null) {
                Field[] declaredFields = obj.get(i).getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    try {
                        map.put(field.getName() + i, field.get(obj.get(i)));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
    }

    /**
     * 多个PDF合并功能
     *  
     *
     * @param files            多个PDF的文件路径
     * @param os               生成的输出流
     */
    public static void concatPDFs(String[] files, OutputStream os) {
        Document document = new Document();
        try {
            PdfCopy copy = new PdfCopy(document, os);
            document.open();
            for (int i = 0; i < files.length; i++) {
                // 首先要判断文件是否存在
                File file = new File(files[i]);
                if (file.exists()) {
                    PdfReader reader = new PdfReader(files[i]);
                    int n = reader.getNumberOfPages();
                    for (int j = 1; j <= n; j++) {
                        document.newPage();
                        PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    /**
     * 合并多个PDF文件 并进行分页
     *  
     *
     * @param streamOfPDFFiles            多文件字节流
     * @param outputStream                输出文件流
     * @param paginate                    是否显示页数
     */
    public static void concatPDFs(List<InputStream> streamOfPDFFiles, OutputStream outputStream, boolean paginate) {
        Document document = new Document();
        try {
            List<PdfReader> readers = new ArrayList<PdfReader>();
            int totalPages = 0;
            Iterator<InputStream> iteratorPDFs = streamOfPDFFiles.iterator();
            // 创建一个pdfReader
            while (iteratorPDFs.hasNext()) {
                InputStream pdf = iteratorPDFs.next();
                PdfReader pdfReader = new PdfReader(pdf);
                totalPages += pdfReader.getNumberOfPages();
                readers.add(pdfReader);
            }
            // 创建文件输出对象
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            document.open();
            // 用于保存PDF数据
            PdfContentByte cb = writer.getDirectContent();
            PdfImportedPage page = null;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPDF = 0;
            Iterator<PdfReader> iteratorPDFReader = readers.iterator();
            // 循环输出
            while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();
                // 创建一个新页
                while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPDF++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader, pageOfCurrentReaderPDF);
                    cb.addTemplate(page, 0, 0);
                    // 分页
                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 8);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER,
                                "Page " + currentPageNumber + " of " + totalPages, 520, 5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPDF = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     *  
     * 设置成只读权限
     *  
     *
     * @param pdfWriter
     */
    public static PdfWriter setReadOnlyPDFFile(PdfWriter pdfWriter) throws DocumentException {

        pdfWriter.setEncryption(null, null, PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);

        return pdfWriter;
    }

    /**
     *  
     * 变更一个图片对象的展示位置和角度信息
     *  
     *
     * @param waterMarkImage  
     * @param xPosition       
     * @param yPosition       
     * @return
     */

    public static Image getWaterMarkImage(Image waterMarkImage, float xPosition, float yPosition) {

        waterMarkImage.setAbsolutePosition(xPosition, yPosition);// 坐标

        waterMarkImage.setRotation(-20);// 旋转 弧度

        waterMarkImage.setRotationDegrees(-45);// 旋转 角度

        waterMarkImage.scalePercent(100);// 依照比例缩放

        return waterMarkImage;
    }

    /**
     * 删除临时文件
     *  
     *
     * @param fileName
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *  
     *
     * @param dir            将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful. If a
     *         deletion fails, the method stops attempting to delete and returns
     *         "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 读取PDF内容
     *  
     *
     * @param filePath
     * @return
     */
    public static String readPDF(String filePath) {
        String content = "";
        int pageNum = 0;
        try {
            PdfReader reader = new PdfReader(filePath);
            pageNum = reader.getNumberOfPages();// 获得页数
            for (int i = 1; i < pageNum; i++) {// 只能从第1页开始读
                content += PdfTextExtractor.getTextFromPage(reader, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 设置页脚页码
     *  
     *
     * @param writer
     */
    public static void setFooter(PdfWriter writer) {
        writer.setBoxSize("art", PageSize.A4);
        writer.setPageEvent(new HeaderAndFooter());
    }

    /**
     * 获得页眉页脚
     *  
     *
     * @return
     */
    public static HeaderAndFooter getHeaderAndFooter() {
        return new HeaderAndFooter();
    }

    public static class HeaderAndFooter extends PdfPageEventHelper {
        // 页眉
        public String header = "";
        // 预设文档字体大小，页脚页眉最好和文本大小一致
        public int presentFontSize = 10;
        // 文档页面大小，最好前面传入，否则默认为A4纸张
        public Rectangle pageSize = PageSize.A4;
        // 模板
        public PdfTemplate total = null;

        // 设置页眉
        public void setHeader(String header) {
            this.header = header;
        }

        // 设置预设字体
        public void setPresentFontSize(int presentFontSize) {
            this.presentFontSize = presentFontSize;
        }

        // 设置页面大小
        public void setPageSize(Rectangle pageSize) {
            this.pageSize = pageSize;
        }

        // 文档打开时创建模板
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(50, 50);// 共 页
            // 的矩形的长宽高
        }

        // 关闭每页的时候，写入页眉，写入'第几页共'这几个字。
        public void onEndPage(PdfWriter writer, Document document) {
            Font textFont = new Font(bfChinese, presentFontSize, Font.NORMAL);
            // 1.写入页眉
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase(header, textFont),
                    document.left(), document.top() + 20, 0);

            // 2.写入前半部分的 第 X页/共
            int pageS = writer.getPageNumber();
            String foot1 = "第 " + pageS + " 页 /共  ";
            Phrase footer = new Phrase(foot1, textFont);

            // 3.计算前半部分的foot1的长度，后面好定位最后一部分的'Y页'这俩字的x轴坐标，字体长度也要计算进去 = len
            float len = bfChinese.getWidthPoint(foot1, presentFontSize);

            // 4.拿到当前的PdfContentByte
            PdfContentByte cb = writer.getDirectContent();

            // 5.写入页脚1，x轴就是(右margin+左margin + right() -left()- len)/2.0F
            // 再给偏移20F适合人类视觉感受，否则肉眼看上去就太偏左了
            // ,y轴就是底边界-20,否则就贴边重叠到数据体里了就不是页脚了；注意Y轴是从下往上累加的，最上方的Top值是大于Bottom好几百开外的。
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.rightMargin() + document.right() + document.leftMargin() - document.left() - len) / 2.0F
                            + 20F,
                    document.bottom() - 20, 0);

            // 6.写入页脚2的模板（就是页脚的Y页这俩字）添加到文档中，计算模板的和Y轴,X=(右边界-左边界 -
            // 前半部分的len值)/2.0F +
            // len ， y 轴和之前的保持一致，底边界-20
            cb.addTemplate(total,
                    (document.rightMargin() + document.right() + document.leftMargin() - document.left()) / 2.0F + 20F,
                    document.bottom() - 20); // 调节模版显示的位置

        }

        public void onCloseDocument(PdfWriter writer, Document document) {
            // 7.最后一步了，就是关闭文档的时候，将模板替换成实际的 Y 值,至此，page x of y 制作完毕，完美兼容各种文档size。
            total.beginText();
            total.setFontAndSize(bfChinese, presentFontSize);// 生成的模版的字体、颜色
            String foot2 = Integer.toString(writer.getPageNumber()) + " 页";
            total.showText(foot2);// 模版显示的内容
            total.endText();
            total.closePath();
        }
    }

    /**
     *  
     * 为PDF分页时创建添加文本水印的事件信息
     */
    public class TextWaterMarkPdfPageEvent extends PdfPageEventHelper {

        private String waterMarkText;

        public TextWaterMarkPdfPageEvent(String waterMarkText) {

            this.waterMarkText = waterMarkText;

        }

        public void onEndPage(PdfWriter writer, Document document) {

            float pageWidth = document.right() + document.left();// 获取pdf内容正文页面宽度

            float pageHeight = document.top() + document.bottom();// 获取pdf内容正文页面高度

            // 设置水印字体格式
            Font waterMarkFont = new Font(bfChinese);
            waterMarkFont.setSize(5);

            PdfContentByte waterMarkPdfContent = writer.getDirectContent();// 决定了水印图层高低
            Phrase phrase = new Phrase(waterMarkText, waterMarkFont);
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 20; j++) {
                    ColumnText.showTextAligned(waterMarkPdfContent, Element.ALIGN_CENTER, phrase, pageWidth * 0.2f * j,
                            pageHeight * 0.08f * i, 45);
                }
            }

        }

    }

    /**
     *  
     * 为PDF分页时创建添加图片水印的事件信息
     */
    public class PictureWaterMarkPdfPageEvent extends PdfPageEventHelper {

        private String waterMarkFullFilePath;

        private Image waterMarkImage;

        public PictureWaterMarkPdfPageEvent(String waterMarkFullFilePath) {
            this.waterMarkFullFilePath = waterMarkFullFilePath;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            try {
                float pageWidth = document.right() + document.left();// 获取pdf内容正文页面宽度

                float pageHeight = document.top() + document.bottom();// 获取pdf内容正文页面高度

                PdfContentByte waterMarkPdfContent = writer.getDirectContent();

                // 仅设置一个图片实例对象，整个PDF文档只应用一个图片对象，极大减少因为增加图片水印导致PDF文档大小增加

                if (waterMarkImage == null) {

                    waterMarkImage = Image.getInstance(waterMarkFullFilePath);

                }
                // 添加水印图片，文档正文内容采用横向三列，竖向两列模式增加图片水印
                for (int i = 0; i < 100; i++) {
                    for (int j = 0; j < 4; j++) {
                        waterMarkPdfContent.addImage(
                                getWaterMarkImage(waterMarkImage, pageWidth * 0.3f * j, pageHeight * 0.05f * i));
                    }
                }
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.8f);// 设置透明度为0.2
                gs.setStrokeOpacity(0.8f);
                gs.setOverPrintStroking(true);
                waterMarkPdfContent.setGState(gs);
            } catch (DocumentException de) {
                de.printStackTrace();
                System.err.println("pdf watermark font:" + de.getMessage());
            } catch (IOException io) {
                io.printStackTrace();
                System.err.println("pdf watermark font:" + io.getMessage());

            }
        }
    }

}