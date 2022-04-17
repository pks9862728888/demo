package com.example.demo.artifactsmanager.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class PropertiesService {

    @Value("${artifactsBaseDirectory}")
    private String artifactsBaseDirectory;

    @Value("${noOfDaysToStoreArtifacts}")
    private int noOfDaysToStoreArtifacts;

}
