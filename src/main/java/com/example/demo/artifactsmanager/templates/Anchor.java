package com.example.demo.artifactsmanager.templates;

import lombok.NonNull;

import static com.example.demo.artifactsmanager.utils.StringFormatterUtil.emptyIfNull;

public class Anchor {

    private static final String BLANK = "_blank";
    private static final String SELF = "_self";

    private static final String template =
            "<a class=\"$LINK_CSS_CLASS$\" href=\"$LINK_HREF$\" target=\"$TARGET$\">$LINK_BODY$</a>";

    public static String getTemplate(@NonNull String linkHref, @NonNull String linkBody,
                                     String linkCssClass, boolean openInNewTab) {
        return template
                .replace("$LINK_HREF$", linkHref)
                .replace("$LINK_BODY$", linkBody)
                .replace("$LINK_CSS_CLASS$", emptyIfNull(linkCssClass))
                .replace("$TARGET$", openInNewTab ? BLANK : SELF);
    }
}
