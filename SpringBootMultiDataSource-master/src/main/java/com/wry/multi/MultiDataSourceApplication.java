package com.wry.multi;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//去掉自动注册了
@SpringBootApplication(exclude = PageHelperAutoConfiguration.class)
public class MultiDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiDataSourceApplication.class, args);
    }

}
