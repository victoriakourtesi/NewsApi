package com.newsapp.newswebappui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Springboot application responsible for context loading
 * Component scan helps the packages to include in the application context
 */
@SpringBootApplication
@ComponentScan({"com.news.app.newswebapp", "com.newsapp.newswebappui"})
public class NewsWebAppUiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsWebAppUiApplication.class, args);
    }

}
