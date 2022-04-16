package com.example.demo.artifactsmanager.enums;

import lombok.NonNull;

public enum FileExtensionEnum {
    PNG(".png"),
    JPEG(".jpeg");

    private final String extension;

    FileExtensionEnum(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isImage(@NonNull String fileName) {
        return fileName.endsWith(PNG.getExtension()) || fileName.endsWith(JPEG.getExtension());
    }
}
