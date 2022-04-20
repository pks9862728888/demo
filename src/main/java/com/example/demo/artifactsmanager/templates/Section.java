package com.example.demo.artifactsmanager.templates;

import lombok.NonNull;

public class Section {

    private static final String template = "<section class=\"$SECTION_CSS_CLASS$\">\n" +
            "\t$SECTION_BODY$\n" +
            "</section>";

    public static String getTemplate(@NonNull String sectionCssClass, @NonNull String sectionBody) {
        return template
                .replace("$SECTION_CSS_CLASS$", sectionCssClass)
                .replace("$SECTION_BODY$", sectionBody);
    }
}
