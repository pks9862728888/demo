package com.example.demo.artifactsmanager.enums;

public enum DateTimePattern {
    dd_MM_YYYY_hhmmss("dd-MM-YYYY-hh-mm-ss");

    private final String pattern;

    DateTimePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
