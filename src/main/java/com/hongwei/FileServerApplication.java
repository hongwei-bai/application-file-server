package com.hongwei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Deploy 1 - IntelliJ IDEA Debug
@SpringBootApplication
@EnableScheduling
public class FileServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }
}

// Deploy 3 - deploy war to Tomcat/NGINX server
//@EntityScan
//@SpringBootApplication
//@EnableScheduling
//public class FileServerApplication extends SpringBootServletInitializer {
//
//    public static void main(String[] args) {
//        SpringApplication.run(FileServerApplication.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(FileServerApplication.class);
//    }
//}
