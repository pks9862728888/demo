package com.example.demo.artifactsmanager.configuration;

import com.example.demo.artifactsmanager.services.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class StaticFilesExposeConfig implements WebMvcConfigurer {

    @Autowired
    private PropertiesService propertiesService;

    public static final String STATIC_RESOURCES_BASE_PATH = "artifacts";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Artifacts
        registry.addResourceHandler("/" + STATIC_RESOURCES_BASE_PATH + "/**")
                .addResourceLocations("file:" + propertiesService.getArtifactsBaseDirectory());

        // Css
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:css/");
    }
}
