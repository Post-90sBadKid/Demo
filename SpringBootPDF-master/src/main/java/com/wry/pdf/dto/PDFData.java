package com.wry.pdf.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@Data
public class PDFData {
    /**
     * PDF文件标题
     */
    private String title;

    /**
     * 标题行
     */
    private List<String> thead;

    /**
     * 内容
     */
    private List<List<String>> tbody;
}
