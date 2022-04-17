package com.example.demo.artifactsmanager.controllers;

import com.example.demo.artifactsmanager.enums.DateTimePattern;
import com.example.demo.artifactsmanager.models.Directory;
import com.example.demo.artifactsmanager.models.Message;
import com.example.demo.artifactsmanager.models.Screenshot;
import com.example.demo.artifactsmanager.services.DirectoryService;
import com.example.demo.artifactsmanager.services.PropertiesService;
import com.example.demo.artifactsmanager.services.UriBuilderService;
import com.example.demo.artifactsmanager.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.artifactsmanager.configuration.CukeReportExposeConfig.STATIC_RESOURCES_BASE_PATH;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DirectoryController {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private PropertiesService propertiesService;

    @Autowired
    private UriBuilderService uriBuilderService;

    // Directory constants
    public static final String SCREENSHOTS = "screenshots";
    public static final String CUCUMBER_HTML_REPORTS = "cucumber-html-reports";

    // Title constants
    public static final String PRUNE_ALL_EXPIRED_ARTIFACTS = "PRUNE_ALL_EXPIRED_ARTIFACTS";
    public static final String VIEW_ARTIFACTS = "VIEW_ARTIFACTS";
    public static final String DELETE_ALL_ARTIFACTS_FOR_REGRESSION = "DELETE_ALL_ARTIFACTS_FOR_REGRESSION";
    public static final String VIEW_ALL_REGRESSIONS = "VIEW_ALL_REGRESSIONS";
    public static final String DELETE_ARTIFACTS = "DELETE_ARTIFACTS";
    public static final String LIST_SCREENSHOTS = "LIST_SCREENSHOTS";
    public static final String VIEW_CUKE_REPORT = "VIEW_CUKE_REPORT";
    public static final String OVERVIEW_FEATURES_HTML = "overview-features.html";

    // Name constants
    public static final String REGRESSION_NAME = "Regression name: ";
    public static final String REGRESSION_TRIGGER = "Regression trigger: ";

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> listAllRegressionTestCategories(HttpServletRequest request) {
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
                        .viewSpecificRegressionArtifacts(f.getName(), request))
                        .withSelfRel()
                        .withTitle(VIEW_ARTIFACTS)
                        .withName(REGRESSION_NAME + f.getName()));
                // Delete all regression artifacts link
                links.add(linkTo(methodOn(DirectoryController.class)
                        .deleteAllRegressionArtifactsForSpecificRegression(f.getName(), request))
                        .withSelfRel()
                        .withTitle(DELETE_ALL_ARTIFACTS_FOR_REGRESSION)
                        .withName(REGRESSION_NAME + f.getName()));
            });

            // Link to prune all artifacts which are older than specified
            links.add(linkTo(methodOn(DirectoryController.class)
                    .pruneAllExpiredArtifacts(request))
                    .withSelfRel()
                    .withTitle(PRUNE_ALL_EXPIRED_ARTIFACTS));

            message.add(links);
        }

        return new HttpEntity<>(message);
    }

    @GetMapping("/view-artifacts")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> viewSpecificRegressionArtifacts(
            @RequestParam(name = "regressionName") String regressionName,
            HttpServletRequest httpServletRequest) {
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
                    .listAllRegressionTestCategories(httpServletRequest))
                    .withSelfRel()
                    .withTitle(VIEW_ALL_REGRESSIONS));
        } else {
            message.setMessage("Available artifacts for regression run: " + regressionName);

            // Add manage artifact link for each regression run
            regressionRunDir.forEach(f ->
                    links.add(linkTo(methodOn(DirectoryController.class)
                            .manageArtifact(regressionName, f.getName(), httpServletRequest))
                            .withSelfRel()
                            .withTitle(VIEW_ARTIFACTS)
                            .withName(REGRESSION_TRIGGER + regressionName + "/" + f.getName())));

            // Add delete regression link
            links.add(linkTo(methodOn(DirectoryController.class)
                    .deleteAllRegressionArtifactsForSpecificRegression(regressionName, httpServletRequest))
                    .withSelfRel()
                    .withTitle(DELETE_ALL_ARTIFACTS_FOR_REGRESSION)
                    .withName(REGRESSION_NAME + regressionName));
        }
        message.add(links);
        return new HttpEntity<>(message);
    }

    @GetMapping("/manage-artifact")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> manageArtifact(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir,
            HttpServletRequest httpServletRequest) {
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
            if (f.getName().equalsIgnoreCase(SCREENSHOTS)) {
                links.add(linkTo(methodOn(DirectoryController.class)
                        .listScreenshots(regressionName, artifactDir, f.getName(), httpServletRequest))
                        .withSelfRel()
                        .withTitle(LIST_SCREENSHOTS)
                        .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
            } else if (f.getName().equalsIgnoreCase(CUCUMBER_HTML_REPORTS)) {
                links.add(Link.of(uriBuilderService.buildUri(httpServletRequest,
                                STATIC_RESOURCES_BASE_PATH, regressionName, artifactDir, f.getName(), OVERVIEW_FEATURES_HTML))
                        .withTitle(VIEW_CUKE_REPORT)
                        .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
            }
        });

        // Delete artifact dir link
        links.add(linkTo(methodOn(DirectoryController.class)
                .deleteArtifact(regressionName, artifactDir, httpServletRequest))
                .withSelfRel()
                .withTitle(DELETE_ARTIFACTS)
                .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));

        // View all regression trigger artifacts' dir link
        links.add(linkTo(methodOn(DirectoryController.class)
                .viewSpecificRegressionArtifacts(regressionName, httpServletRequest))
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
            @RequestParam(name = "screenshotDir") String screenshotDir,
            HttpServletRequest httpServletRequest) {
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
                    .manageArtifact(regressionName, artifactDir, httpServletRequest))
                    .withSelfRel()
                    .withTitle(VIEW_ARTIFACTS)
                    .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
        } else {
            message.setMessage(String.format("%s screenshots found for regression run: %s/%s/%s", screenshotList.size(),
                    regressionName, artifactDir, screenshotDir));

            // Add link to view each screenshot (served as static data)
            screenshotList.forEach(sc -> {
                links.add(Link.of(uriBuilderService.buildUri(
                                httpServletRequest,
                                STATIC_RESOURCES_BASE_PATH, regressionName, artifactDir, SCREENSHOTS, sc.getScreenshotName()))
                        .withSelfRel()
                        .withTitle("VIEW_SCREENSHOT")
                        .withName(sc.getScreenshotName()));
            });
        }

        // Add link to go back (view regression artifacts)
        links.add(linkTo(methodOn(DirectoryController.class)
                .manageArtifact(regressionName, artifactDir, httpServletRequest))
                .withSelfRel()
                .withTitle(VIEW_ARTIFACTS)
                .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));

        message.add(links);
        return new HttpEntity<>(message);
    }

//    @GetMapping(value = "/view-screenshot", produces = MediaType.IMAGE_PNG_VALUE)
//    public ResponseEntity<byte[]> viewScreenshot(
//            @RequestParam(name = "regressionName") String regressionName,
//            @RequestParam(name = "artifactDir") String artifactDir,
//            @RequestParam(name = "screenshotDir") String screenshotDir,
//            @RequestParam(name = "screenshotName") String screenshotName) throws IOException {
//        // Read file to byte array
//        RandomAccessFile f = new RandomAccessFile(Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName,
//                artifactDir, screenshotDir, screenshotName).toString(), "r");
//        byte[] bytes = new byte[(int) f.length()];
//        f.read(bytes);
//        f.close();
//
//        return new ResponseEntity<>(bytes, HttpStatus.OK);
//    }

    @GetMapping("/delete-artifact-for-regression-run")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> deleteArtifact(
            @RequestParam(name = "regressionName") String regressionName,
            @RequestParam(name = "artifactDir") String artifactDir,
            HttpServletRequest httpServletRequest) {
        System.out.println("/delete-artifact-for-regression-run called for: " + regressionName + "/" + artifactDir);
        Message message = new Message();
        // Delete artifact dir
        if (directoryService.deleteDirectory(
                Paths.get(propertiesService.getArtifactsBaseDirectory(), regressionName, artifactDir).toFile())) {
            // Set message if deleted
            message.setMessage("Directory deleted successfully: " + regressionName + "/" + artifactDir);

            // Link to list all artifacts for specific regression trigger
            message.add(linkTo(methodOn(DirectoryController.class)
                    .viewSpecificRegressionArtifacts(regressionName, httpServletRequest))
                    .withSelfRel()
                    .withTitle(VIEW_ARTIFACTS)
                    .withName(REGRESSION_NAME + regressionName));
        } else {
            // Set message that directory could not be deleted
            message.setMessage("Could not delete directory: " + artifactDir);

            // Link to manage artifacts
            message.add(linkTo(methodOn(DirectoryController.class)
                    .manageArtifact(regressionName, artifactDir, httpServletRequest))
                    .withSelfRel()
                    .withTitle(VIEW_ARTIFACTS)
                    .withName(REGRESSION_TRIGGER + regressionName + "/" + artifactDir));
        }

        // Link to list all regressions
        message.add(linkTo(methodOn(DirectoryController.class)
                .listAllRegressionTestCategories(httpServletRequest))
                .withSelfRel()
                .withTitle(VIEW_ALL_REGRESSIONS));

        return new HttpEntity<>(message);
    }

    @GetMapping("/delete-all-regression-artifacts")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> deleteAllRegressionArtifactsForSpecificRegression(
            @RequestParam(name = "regressionName") String regressionName,
            HttpServletRequest httpServletRequest) {
        System.out.println("/delete-all-regression-artifacts called for regressionName: " + regressionName);
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
                .listAllRegressionTestCategories(httpServletRequest))
                .withSelfRel()
                .withTitle(VIEW_ALL_REGRESSIONS));

        return new HttpEntity<>(message);
    }

    @GetMapping("/prune-expired-artifacts")
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<Message> pruneAllExpiredArtifacts(HttpServletRequest httpServletRequest) {
        System.out.println("/prune-expired-artifacts endpoint called");

        // List all regression runs for specified regression
        ArrayList<Directory> regressionRuns = directoryService.listAllAvailableArtifactsDesc(
                propertiesService.getArtifactsBaseDirectory());

        // Find expiry date of artifacts
        String artifactExpiryDate = DateTimeUtils.getDaysMinus(DateTimePattern.dd_MM_YYYY_hhmmss,
                propertiesService.getNoOfDaysToStoreArtifacts());
        List<String> deletedArtifacts = new ArrayList<>();

        // Delete expired artifacts and create message
        for (Directory regression : regressionRuns) {
            Path regTriggerPath = Paths.get(propertiesService.getArtifactsBaseDirectory(), regression.getName());

            // Delete all regression trigger having date older than artifactExpiryDate
            directoryService.listAllAvailableArtifactsDesc(regTriggerPath.toString()).stream()
                    .filter(dir -> dir.getName().compareTo(artifactExpiryDate) < 0)
                    .map(dir -> regTriggerPath.resolve(dir.getName()))
                    .forEach(path -> {
                        if (directoryService.deleteDirectory(path.toFile())) {
                            deletedArtifacts.add(path.toString());
                        } else {
                            System.out.println("ERROR: Unable to delete directory: " + path);
                        }
                    });

            // Delete the empty regression dir if no regression trigger artifacts are present
            directoryService.deleteDirectoryIfEmpty(regTriggerPath.toFile());
        }

        // Prepare message
        Message message = new Message();
        if (deletedArtifacts.isEmpty()) {
            message.setMessage("No regression artifacts found which are triggered before timestamp: "
                    + artifactExpiryDate);
        } else {
            message.setMessage("Deleted artifacts: [" + String.join(", ", deletedArtifacts) + "]");
            System.out.println(message.getMessage());
        }
        message.add(linkTo(methodOn(DirectoryController.class)
                .listAllRegressionTestCategories(httpServletRequest))
                .withSelfRel()
                .withTitle(VIEW_ALL_REGRESSIONS));
        return new HttpEntity<>(message);
    }
}
