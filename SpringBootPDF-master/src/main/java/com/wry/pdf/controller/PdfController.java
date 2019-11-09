package com.wry.pdf.controller;

import com.wry.pdf.dto.PDFData;
import com.wry.pdf.utils.PDFUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping("/exportPDF")
    public void exportPDF(HttpServletResponse response) {
        //获取数据
        PDFData data = new PDFData();
        //设置PDF文件的标题
        data.setTitle("2019-02-01的科室预算");
        //设置表格标题
        List<String> theadList = getPDFTitles();
        data.setThead(theadList);
        //设置表格数据
        List<List<String>> tboodList = getPDFBody(null);
        data.setTbody(tboodList);

        //设置导出文件名称
        String fileName = "科室预设" + System.currentTimeMillis() + ".pdf";

        PDFUtils.exportExcel(response, fileName, data);


    }

    /**
     * 获取PDF 标题 (根据需求实现)
     *
     * @return
     */
    private List<String> getPDFTitles() {
        List<String> titles = new ArrayList<>();
        titles.add("科室名称");
        titles.add("科室编码");
        titles.add("科室Id");
        return titles;
    }

    /**
     * 获取PDF 的内容 (根据需求实现)
     *
     * @param objectList
     * @return
     */
    private List<List<String>> getPDFBody(List<Object> objectList) {
        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<String> stringList = new ArrayList<>();
            stringList.add("科室" + i);
            stringList.add("编码" + i);
            stringList.add("Id-" + i);
            result.add(stringList);
        }

        return result;
    }
}
