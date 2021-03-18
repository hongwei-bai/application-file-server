package com.hongwei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

// Deploy 1 - IntelliJ IDEA Debug
//@SpringBootApplication
//@EnableScheduling
//public class FileServerApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(FileServerApplication.class, args);
//    }
//
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        DataSize maxSize = DataSize.ofMegabytes(10);
//        DataSize requestMaxSize = DataSize.ofMegabytes(30);
//        factory.setMaxFileSize(maxSize);
//        factory.setMaxRequestSize(requestMaxSize);
//        return factory.createMultipartConfig();
//    }
//}

// Deploy 3 - deploy war to Tomcat/NGINX server
@EntityScan
@SpringBootApplication
@EnableScheduling
public class FileServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FileServerApplication.class);
    }
}
