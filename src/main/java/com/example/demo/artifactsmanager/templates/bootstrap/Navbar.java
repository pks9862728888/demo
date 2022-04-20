package com.example.demo.artifactsmanager.templates.bootstrap;

import lombok.NonNull;

public class Navbar {

    private static final String template = "<nav class=\"$NAVBAR_CSS_CLASS$\">\n" +
            "\t<a class=\"navbar-brand\" href=\"$TITLE_LINK$\">$TITLE_NAME$</a>\n" +
            "\t<a href=\"%LOGOUT_LINK%\"><button class=\"btn btn-outline-warning my-2 my-sm-0\" type=\"submit\">$BUTTON_TEXT$</button></a>\n" +
            "</nav>";

    public static String getTemplate(@NonNull String titleLink, @NonNull String buttonLink,
                                     @NonNull String title, @NonNull String navbarCssClass,
                                     @NonNull String buttonText) {
        return template
                .replace("$NAVBAR_CSS_CLASS$", navbarCssClass)
                .replace("$TITLE_LINK$", titleLink)
                .replace("%LOGOUT_LINK%", buttonLink)
                .replace("$TITLE_NAME$", title)
                .replace("$BUTTON_TEXT$", buttonText);
    }
}
