package com.wry.excel.controller;


import com.wry.excel.dto.ExcelData;
import com.wry.excel.util.ExportExcelUtils;
import com.wry.excel.util.ImportExcelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    @RequestMapping(value = "/fileUpload")
    @ResponseBody
    public String fileUpload(HttpSession session, @RequestParam("fileName") MultipartFile file, @RequestParam Integer token) throws Exception {
        if (file.isEmpty()) {
            return "false";
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String newfileName = df.format(new Date()) + suffix;

        String path = "C:/drgssptUpfile";
        File dest = new File(path + "/" + newfileName);
        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }

        //保存文件
        file.transferTo(dest);
        //生成的文件
        InputStream input = new FileInputStream(dest);

        //保存读取表格的数据
        List<Map> excelList = null;
        switch (suffix) {
            case ".xls":
                excelList = ImportExcelUtil.readXls(input);
                break;
            case ".xlsx":
                excelList = ImportExcelUtil.readXlsx(input);
                break;
            default:
                break;
        }

        //token 1表示处理那个数据
        //处理表格数据
        if (token == 1) {
            if (excelList==null){
                System.out.println("获取表格数据失败无法保存");
                return "false";
            }
            //进行业务处理
            for (Map map : excelList) {
                System.out.println(map+"*******");
            }
            return "true";
        }

        return "false";
    }


    @RequestMapping(value = "/excelExport", method = RequestMethod.GET)
    public String excel(HttpServletResponse response) throws Exception {
        ExcelData data = new ExcelData();
        String sheetName="院办公室";
        data.setName(sheetName);
        //标题
        List<String> titles = getTitleList();
        data.setTitles(titles);
        //从业务层获取数据（这里自定义）
        List<Map> studentMap=getStudent();
        //设置数据
        List<List<Object>> rows = getRowList(studentMap);
        //最后一行
        getLastRow(rows);

        data.setRows(rows);


        String fileName = "Excel"+System.currentTimeMillis()+".xlsx";
        ExportExcelUtils.exportExcel(response, fileName, data);
        return "true";
    }

    /**
     * 获取数据列
     * @param studentMap
     * @return
     */
    private List<List<Object>> getRowList(List<Map> studentMap) {
        List<List<Object>> rows = new ArrayList();
        for (Map map : studentMap) {
            List<Object> row = new ArrayList();
            row.add(map.get("code"));
            row.add(map.get("name"));
            row.add(map.get("position"));
            //添加到集合
            rows.add(row);
        }
        return rows;
    }

    /**
     * 获取数据（应该从业务层获取，此处做测试）
     * @return
     */
    private List<Map> getStudent() {
        List<Map> list=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            Map map=new HashMap();
            map.put("code","A"+i);
            map.put("name","张三"+i);
            map.put("position","职位"+i);
            list.add(map);
        }

        return list;
    }

    /**
     * 设置最后一行数据
     * @param rows
     */
    private void getLastRow(List<List<Object>> rows) {
        List<Object> row = new ArrayList<>();

        row.add("合计");
        row.add(rows.size());
        row.add(rows.size());

        rows.add(row);
    }

    /**
     * 获取每一行中的一个值
     * @param row
     * @param index
     * @param value
     * @return
     */
    private Double getRowValue(List row,Integer index,Double value){
        //夜班费
        Object Object = row.get(index);
        if (Object != null && !Object.toString().equals("")) {
            value = Double.parseDouble(Object.toString());
        }
        return  value;
    }




    /**
     * 设置导出的标题
     * @return
     */
    private  List<String> getTitleList() {
        List<String> titles = new ArrayList();
        titles.add("编号");
        titles.add("姓名");
        titles.add("职位");

        return titles;

    }

    /**
     * 拷贝文件
     *
     * @param srcPathStr
     * @param desPathStr
     * @param newFileName
     */
    private static void copyFile(String srcPathStr, String desPathStr, String newFileName) {
        desPathStr = desPathStr + File.separator + newFileName; //源文件地址
        System.out.println(desPathStr);

        try {
            //2.创建输入输出流对象
            FileInputStream fis = new FileInputStream(srcPathStr);
            FileOutputStream fos = new FileOutputStream(desPathStr);

            //创建搬运工具
            byte datas[] = new byte[1024 * 8];
            //创建长度
            int len = 0;
            //循环读取数据
            while ((len = fis.read(datas)) != -1) {
                fos.write(datas, 0, len);
            }
            //3.释放资源
            fis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
