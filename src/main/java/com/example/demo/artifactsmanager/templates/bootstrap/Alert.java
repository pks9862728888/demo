package com.example.demo.artifactsmanager.templates.bootstrap;

import lombok.NonNull;

public class Alert {

    private static final String template = "<div class=\"$CSS_CLASS$\" role=\"alert\">\n" +
            "\t$ALERT_BODY$\n" +
            "</div>";

    public static String getTemplate(@NonNull String alertBody, @NonNull String alertCssClass) {
        return template.replace("$CSS_CLASS$", alertCssClass)
                .replace("$ALERT_BODY$", alertBody);
    }
}
