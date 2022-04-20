package com.example.demo.artifactsmanager.utils;

import java.util.Objects;

public interface StringFormatterUtil {

    static String emptyIfNull(String str) {
        return Objects.isNull(str) ? "" : str;
    }
}
