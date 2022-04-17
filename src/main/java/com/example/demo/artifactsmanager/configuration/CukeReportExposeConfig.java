package com.example.demo.artifactsmanager.configuration;

import com.example.demo.artifactsmanager.services.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CukeReportExposeConfig implements WebMvcConfigurer {

    @Autowired
    private PropertiesService propertiesService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/gui-test/cucumber-html-reports/**")
                .addResourceLocations("file:artifacts/gui-test/16-04-2022-12-12-23/cucumber-html-reports/");
        registry.addResourceHandler("/smoke-test/cucumber-html-reports/**")
                .addResourceLocations("file:artifacts/smoke-test/16-04-2022-12-12-23/cucumber-html-reports/");
    }
}
