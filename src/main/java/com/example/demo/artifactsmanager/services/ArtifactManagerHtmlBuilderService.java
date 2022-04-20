package com.example.demo.artifactsmanager.services;

import com.example.demo.artifactsmanager.models.Message;
import com.example.demo.artifactsmanager.templates.Anchor;
import com.example.demo.artifactsmanager.templates.Html;
import com.example.demo.artifactsmanager.templates.LinkTemplate;
import com.example.demo.artifactsmanager.templates.Section;
import com.example.demo.artifactsmanager.templates.bootstrap.*;
import com.example.demo.artifactsmanager.utils.BootstrapCssClassNames;
import lombok.NonNull;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static com.example.demo.artifactsmanager.controllers.DirectoryController.VIEW_CUKE_REPORT;
import static com.example.demo.artifactsmanager.controllers.DirectoryController.VIEW_SCREENSHOT;

@Service
public class ArtifactManagerHtmlBuilderService {

    public static final String ARTIFACT_MANAGER = "Artifact Manager";
    private String CSS_LINKS;

    @PostConstruct
    protected void init() {
        // Pre-render repeated components to save processing time
        // CSS links
        CSS_LINKS = LinkTemplate.getTemplate("css/bootstrap4.min.css");
        CSS_LINKS += "\t" + LinkTemplate.getTemplate("css/artifactmanager.css");
    }

    public String buildWebPage(@NonNull Message message, @NonNull String titleLink,
                               @NonNull String logoutLink) {
        // Navbar
        String navbar = Navbar.getTemplate(
                titleLink,
                logoutLink,
                ARTIFACT_MANAGER,
                BootstrapCssClassNames.NAVBAR_BG_DARK + " navbarcolor sticky",
                "Logout"
        );

        // Alert
        String alert = Alert.getTemplate(message.getMessage(), BootstrapCssClassNames.ALERT_SUCCESS_CSS);

        // Cards
        StringBuilder cards = new StringBuilder();
        for (Link link : message.getLinks()) {
            cards.append(Anchor.getTemplate(
                    link.getHref(),
                    BasicCard.getTemplate(
                            Objects.requireNonNull(link.getTitle()),
                            link.getName(),
                            "link"
                    ),
                    "card-a",
                    shouldOpenLinkInNewTab(link.getTitle())
            )).append("\n");
        }

        // Main section
        String bodyTemplate = navbar + "\n" + Section.getTemplate(
                "main",
                alert + "\n" + cards
        );
        return Html.getTemplate(
                ARTIFACT_MANAGER,
                CSS_LINKS,
                bodyTemplate
        );
    }

    private boolean shouldOpenLinkInNewTab(@NonNull String title) {
        if (title.equals(VIEW_CUKE_REPORT) || title.equals(VIEW_SCREENSHOT)) {
            return true;
        }
        return false;
    }

}
