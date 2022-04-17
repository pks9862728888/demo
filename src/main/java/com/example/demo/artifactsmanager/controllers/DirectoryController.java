package com.example.demo.artifactsmanager.controllers;

import com.example.demo.artifactsmanager.models.Directory;
import com.example.demo.artifactsmanager.models.Message;
import com.example.demo.artifactsmanager.models.Screenshot;
import com.example.demo.artifactsmanager.services.DirectoryService;
import com.example.demo.artifactsmanager.services.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DirectoryController {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private PropertiesService propertiesService;

    // Title constants
    public static final String VIEW_ARTIFACTS = "VIEW_ARTIFACTS";
    public static final String DELETE_ALL_ARTIFACTS_FOR_REGRESSION = "DELETE_ALL_ARTIFACTS_FOR_REGRESSION";
    public static final String VIEW_ALL_REGRESSIONS = "VIEW_ALL_REGRESSIONS";
    public static final String DELETE_ARTIFACTS = "DELETE_ARTIFACTS";
    public static final String LIST_SCREENSHOTS = "LIST_SCREENSHOTS";

    // Name constants
    public static final String REGRESSION_NAME = "Regression name: ";
    public static final String REGRESSION_TRIGGER = "Regression trigger: ";

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> listAllRegressionTestCategories() {
        // List all regression directories
        ArrayList<Directory> directoryList = directoryService.listAllAvailableArtifactsDesc(
                propertiesService.getArtifactsBaseDirectory());

        // Create Message
        Message message = new Message();
        if (directoryList.isEmpty()) {
            message.setMessage("No regression artifacts are found.");
        } else {
            message.setMessage("Regression artifacts:");

            // Append links
            List<Link> links = new ArrayList<>();
            directoryList.forEach(f -> {
                // View artifacts link
                links.add(linkTo(methodOn(DirectoryController.class)
                        .viewSpecificRegressionArtifacts(f.getName()))
                        .withSelfRel()
                        .withTitle(VIEW_ARTIFACTS)
                        .withName(REGRESSION_NAME + f.getName()));
                // Delete all regression artifacts link
                links.add(linkTo(methodOn(DirectoryController.class)
                        .deleteAllRegressionArtifactsForSpecificRegression(f.getName()))
                        .withSelfRel()
                        .withTitle(DELETE_ALL_ARTIFACTS_FOR_REGRESSION)
                        .withName(REGRESSION_NAME + f.getName()));
            });
            message.add(links);
        }

        return new HttpEntity<>(message);
    }

    @GetMapping("/view-artifacts")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> viewSpecificRegressionArtifacts(
            @RequestParam(name = "regressionName") String regressionName) {
        // List all regression runs for specified regression
        ArrayList<Directory> regressionRunDir = directoryService.listAllAvailableArtifactsDesc(
                Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName).toString());

        // Create message
        Message message = new Message();

        // Append links
        List<Link> links = new ArrayList<>();
        if (regressionRunDir.isEmpty()) {
            message.setMessage("No artifacts found for regression run: " + regressionName);

            // View all regression link
            links.add(linkTo(methodOn(DirectoryController.class)
                    .listAllRegressionTestCategories())
                    .withSelfRel()
                    .withTitle(VIEW_ALL_REGRESSIONS));
        } else {
            message.setMessage("Available artifacts for regression run: " + regressionName);

            // Add manage artifact link for each regression run
            regressionRunDir.forEach(f ->
                    links.add(linkTo(methodOn(DirectoryController.class)
                            .manageArtifact(regressionName, f.getName()))
                            .withSelfRel()
                            .withTitle(VIEW_ARTIFACTS)
                            .withName(REGRESSION_TRIGGER + regressionName + "/" + f.getName())));

            // Add delete regression link
            links.add(linkTo(methodOn(DirectoryController.class)
                    .deleteAllRegressionArtifactsForSpecificRegression(regressionName))
                    .withSelfRel()
                    .withTitle(DELETE_ALL_ARTIFACTS_FOR_REGRESSION)
                    .withName(REGRESSION_NAME + regressionName));
        }
        message.add(links);
        return new HttpEntity<>(message);
    }

    @GetMapping("/delete-all-regression-artifacts")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> deleteAllRegressionArtifactsForSpecificRegression(
            @RequestParam(name = "regressionName") String regressionName) {
        Message message = new Message();

        // Delete regression dir
        if (directoryService.deleteDirectory(
                Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName).toFile())) {
            message.setMessage("All regression artifacts deleted for regression: " + regressionName);
        } else {
            message.setMessage("Unable to delete regression artifacts for regression: " + regressionName);
        }

        // Link to list all regressions
        message.add(linkTo(methodOn(DirectoryController.class)
                .listAllRegressionTestCategories())
                .withSelfRel()
                .withTitle(VIEW_ALL_REGRESSIONS));

        return new HttpEntity<>(message);
    }

    @GetMapping("/manage-artifact")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> manageArtifact(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir) {
        // List all directories
        ArrayList<Directory> directoryList = directoryService.listAllAvailableArtifactsDesc(
                Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName, artifactDir).toString());

        // Create message object
        Message message = new Message("Manage artifact for regression run: " + regressionName + "/" + artifactDir);

        // Add links
        List<Link> links = new ArrayList<>();

        // Links for managing artifacts
        directoryList.forEach(f -> {
            // Add the links
            if (f.getName().equalsIgnoreCase("screenshots")) {
                links.add(linkTo(methodOn(DirectoryController.class)
                        .listScreenshots(regressionName, artifactDir, f.getName()))
                        .withSelfRel()
                        .withTitle(LIST_SCREENSHOTS)
                        .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
            }
        });

        // Delete artifact dir link
        links.add(linkTo(methodOn(DirectoryController.class)
                .deleteArtifact(regressionName, artifactDir))
                .withSelfRel()
                .withTitle(DELETE_ARTIFACTS)
                .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));

        // View all regression trigger artifacts' dir link
        links.add(linkTo(methodOn(DirectoryController.class)
                .viewSpecificRegressionArtifacts(regressionName))
                .withSelfRel()
                .withTitle(VIEW_ARTIFACTS)
                .withName(REGRESSION_NAME + regressionName));

        message.add(links);
        return new HttpEntity<>(message);
    }

    @GetMapping("/list-screenshots")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> listScreenshots(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir,
            @RequestParam(name = "screenshotDir") String screenshotDir) {
        // List all screenshots
        ArrayList<Screenshot> screenshotList = directoryService.listAllScreenshots(
                Paths.get(propertiesService.getArtifactsBaseDirectory(),
                        regressionName, artifactDir, screenshotDir).toString());

        // Create Response message
        Message message = new Message();
        List<Link> links = new ArrayList<>();
        if (screenshotList.isEmpty()) {
            message.setMessage(String.format("No screenshots found for regression run: %s/%s", regressionName, artifactDir));

            // Link to manage artifacts
            links.add(linkTo(methodOn(DirectoryController.class)
                    .manageArtifact(regressionName, artifactDir))
                    .withSelfRel()
                    .withTitle(VIEW_ARTIFACTS)
                    .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
        } else {
            message.setMessage(String.format("%s screenshots found for regression run: %s/%s/%s", screenshotList.size(),
                    regressionName, artifactDir, screenshotDir));

            // Add link to view each screenshot
            screenshotList.forEach(sc -> {
                try {
                    links.add(linkTo(methodOn(DirectoryController.class)
                            .viewScreenshot(regressionName, artifactDir, screenshotDir, sc.getScreenshotName()))
                            .withSelfRel()
                            .withTitle("VIEW_SCREENSHOT")
                            .withName(sc.getScreenshotName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        // Add link to go back (view regression artifacts)
        links.add(linkTo(methodOn(DirectoryController.class)
                .manageArtifact(regressionName, artifactDir))
                .withSelfRel()
                .withTitle(VIEW_ARTIFACTS)
                .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));

        message.add(links);
        return new HttpEntity<>(message);
    }

    @GetMapping(value = "/view-screenshot", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> viewScreenshot(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir,
            @RequestParam(name = "screenshotDir") String screenshotDir,
            @RequestParam(name = "screenshotName") String screenshotName) throws IOException {
        // Read file to byte array
        RandomAccessFile f = new RandomAccessFile(Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName,
                artifactDir, screenshotDir, screenshotName).toString(), "r");
        byte[] bytes = new byte[(int) f.length()];
        f.read(bytes);
        f.close();

        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @GetMapping("/delete-artifact-for-regression-run")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> deleteArtifact(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir) {
        Message message = new Message();
        // Delete artifact dir
        if (directoryService.deleteDirectory(
                Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName, artifactDir).toFile())) {
            // Set message if deleted
            message.setMessage("Directory deleted successfully: " + regressionName + "/" + artifactDir);

            // Link to list all artifacts for specific regression trigger
            message.add(linkTo(methodOn(DirectoryController.class)
                    .viewSpecificRegressionArtifacts(regressionName))
                    .withSelfRel()
                    .withTitle(VIEW_ARTIFACTS)
                    .withName(REGRESSION_NAME + regressionName));
        } else {
            // Set message that directory could not be deleted
            message.setMessage("Could not delete directory: " + artifactDir);

            // Link to manage artifacts
            message.add(linkTo(methodOn(DirectoryController.class)
                    .manageArtifact(regressionName, artifactDir))
                    .withSelfRel()
                    .withTitle(VIEW_ARTIFACTS)
                    .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
        }

        // Link to list all regressions
        message.add(linkTo(methodOn(DirectoryController.class)
                .listAllRegressionTestCategories())
                .withSelfRel()
                .withTitle(VIEW_ALL_REGRESSIONS));

        return new HttpEntity<>(message);
    }
}
