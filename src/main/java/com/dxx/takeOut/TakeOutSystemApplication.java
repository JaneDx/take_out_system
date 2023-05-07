package com.dxx.takeOut;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan //加上才会扫描webFilter等组件
@EnableTransactionManagement  //开启事务
@EnableCaching //开启注解缓存
public class TakeOutSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakeOutSystemApplication.class, args);
        log.info("项目启动");
    }

}
