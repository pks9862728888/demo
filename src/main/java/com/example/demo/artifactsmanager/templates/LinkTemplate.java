package com.example.demo.artifactsmanager.templates;

import lombok.NonNull;

public class LinkTemplate {

    private static final String template = "<link rel=\"stylesheet\" href=\"$CSS_FILE_PATH$\">\n";

    public static String getTemplate(@NonNull String cssFilePath) {
        return template.replace("$CSS_FILE_PATH$", cssFilePath);
    }

}
