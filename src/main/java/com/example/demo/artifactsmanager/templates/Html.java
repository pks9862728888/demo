package com.example.demo.artifactsmanager.templates;

import lombok.NonNull;

public class Html {

    private static final String template = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "\t<head>\n" +
            "\t<meta charset=\"utf-8\">\n" +
            "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "\t<title>$TITLE_NAME$</title>\n" +
            "\t<meta name=\"description\" content=\"Artifact Manager Web Page\">\n" +
            "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "\t$CSS_LINKS$\n" +
            "\t</head>\n" +
            "\t<body>\n" +
            "\t\t$BODY_TEMPLATE$\n" +
            "\t</body>\n" +
            "</html>";

    public static String getTemplate(@NonNull String title,
                                     @NonNull String cssLinkTemplate,
                                     @NonNull String bodyTemplate) {
        return template
                .replace("$TITLE_NAME$", title)
                .replace("$CSS_LINKS$", cssLinkTemplate)
                .replace("$BODY_TEMPLATE$", bodyTemplate);
    }
}
