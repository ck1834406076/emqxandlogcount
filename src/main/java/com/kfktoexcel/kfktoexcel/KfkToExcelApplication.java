package com.kfktoexcel.kfktoexcel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
//该注解会扫描相应的包
@ServletComponentScan
@EnableWebMvc
//@EnableScheduling
public class KfkToExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(KfkToExcelApplication.class, args);
    }

}
