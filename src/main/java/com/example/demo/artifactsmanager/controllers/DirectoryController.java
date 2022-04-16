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
                        .withTitle("view_artifacts_for_regression: " + f.getName())
                        .withName(f.getName()));
                // Delete all regression artifacts link
                links.add(linkTo(methodOn(DirectoryController.class)
                        .deleteAllRegressionArtifactsForSpecificRegression(f.getName()))
                        .withSelfRel()
                        .withTitle("delete_all_artifacts_for_regression:" + f.getName())
                        .withName(f.getName()));
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
                    .withTitle("view_all_regression_artifacts"));
        } else {
            message.setMessage("Available artifacts for regression run: " + regressionName);

            // Add delete regression link
            links.add(linkTo(methodOn(DirectoryController.class)
                    .deleteAllRegressionArtifactsForSpecificRegression(regressionName))
                    .withSelfRel()
                    .withTitle("delete_all_artifacts_for_regression: " + regressionName)
                    .withName(regressionName));

            // Add manage artifact link for each regression run
            regressionRunDir.forEach(f ->
                    links.add(linkTo(methodOn(DirectoryController.class)
                            .manageArtifact(regressionName, f.getName()))
                            .withSelfRel()
                            .withTitle("view_artifacts_for_regression_run: " + f.getName())
                            .withName(f.getName())));
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
                .withTitle("view_all_regression_artifacts"));

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

        // Delete artifact dir link
        links.add(linkTo(methodOn(DirectoryController.class)
                .deleteArtifact(regressionName, artifactDir))
                .withSelfRel()
                .withTitle("delete_artifact_dir: " + artifactDir)
                .withName(artifactDir));

        // Links for managing artifacts
        directoryList.forEach(f -> {
            // Add the links
            if (f.getName().equalsIgnoreCase("screenshots")) {
                links.add(linkTo(methodOn(DirectoryController.class)
                        .listScreenshots(regressionName, artifactDir, f.getName()))
                        .withSelfRel()
                        .withTitle("list_screenshots: " + artifactDir)
                        .withName(artifactDir));
            } else if (f.getName().contains("cucumber-html-reports")) {
                links.add(linkTo(methodOn(DirectoryController.class)
                        .viewCukeReport(regressionName, artifactDir, f.getName()))
                        .withSelfRel()
                        .withTitle("view_cuke_report: " + artifactDir)
                        .withName(artifactDir));
            }
        });

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
                    .withTitle("view_all_artifacts_for_regression_run: " + artifactDir)
                    .withName(artifactDir));
        } else {
            message.setMessage(String.format("%s screenshots found for regression run: %s/%s/%s", screenshotList.size(),
                    regressionName, artifactDir, screenshotDir));

            // Add link to view each screenshot
            screenshotList.forEach(sc -> {
                try {
                    links.add(linkTo(methodOn(DirectoryController.class)
                            .viewScreenshot(regressionName, artifactDir, screenshotDir, sc.getScreenshotName()))
                            .withSelfRel()
                            .withTitle("view_screenshot: " + sc.getScreenshotName())
                            .withName(sc.getScreenshotName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
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

    @GetMapping("/view-cuke-report")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<String> viewCukeReport(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir,
            @RequestParam(name = "cukeReportDir") String cukeReportDir) {
        return new HttpEntity<>(cukeReportDir);
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
            message.setMessage("Directory deleted successfully: " + artifactDir);

            // Link to list all artifacts
            message.add(linkTo(methodOn(DirectoryController.class)
                    .viewSpecificRegressionArtifacts(regressionName))
                    .withSelfRel()
                    .withTitle("view_artifacts_for_regression: " + regressionName)
                    .withName(regressionName));
        } else {
            // Set message that directory could not be deleted
            message.setMessage("Could not delete directory: " + artifactDir);

            // Link to manage artifacts
            message.add(linkTo(methodOn(DirectoryController.class)
                    .manageArtifact(regressionName, artifactDir))
                    .withSelfRel()
                    .withTitle("view_artifacts_for_regression_run: " + artifactDir)
                    .withName(artifactDir));
        }
        return new HttpEntity<>(message);
    }
}
