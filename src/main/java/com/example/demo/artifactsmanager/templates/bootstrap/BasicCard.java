package com.example.demo.artifactsmanager.templates.bootstrap;

import lombok.NonNull;

import static com.example.demo.artifactsmanager.utils.StringFormatterUtil.emptyIfNull;

public class BasicCard {

    private static final String template = "<div class=\"card $CARD_CSS$\">\n" +
            "        <p class=\"card-header\">$CARD_HEADER$</p>\n" +
            "        <div class=\"card-body\">\n" +
            "            <p class=\"card-subtitle\">$CARD_BODY$</p>\n" +
            "        </div>\n" +
            "    </div>";

    public static String getTemplate(@NonNull String cardHeader, String cardBody,
                                     String cardCustomCssClass) {
        return template
                .replace("$CARD_HEADER$", cardHeader)
                .replace("$CARD_BODY$", emptyIfNull(cardBody))
                .replace("$CARD_CSS$", emptyIfNull(cardCustomCssClass));
    }

}
