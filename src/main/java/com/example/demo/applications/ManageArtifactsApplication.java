package com.example.demo.applications;

import com.example.demo.artifactsmanager.services.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;

@SpringBootApplication
@ComponentScan(
        basePackages = "com.example.demo.artifactsmanager.*"
)
public class ManageArtifactsApplication implements CommandLineRunner {

    @Autowired
    private PropertiesService propertiesService;

    public static void main(String[] args) {
        SpringApplication.run(ManageArtifactsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        File artifactsDir = new File(propertiesService.getArtifactsBaseDirectory());
        if (!artifactsDir.exists()) {
            boolean directoriesCreated = artifactsDir.mkdirs();
            if (!directoriesCreated) {
                System.out.println("Failed to create directory: " + artifactsDir);
                System.exit(1);
            } else {
                System.out.println("Directory created: " + artifactsDir);
            }
        } else {
            System.out.println("Directory exists: " + artifactsDir);
        }
    }
}
