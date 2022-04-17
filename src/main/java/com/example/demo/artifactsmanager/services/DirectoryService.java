package com.example.demo.artifactsmanager.services;

import com.example.demo.artifactsmanager.enums.FileExtensionEnum;
import com.example.demo.artifactsmanager.models.Directory;
import com.example.demo.artifactsmanager.models.Screenshot;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DirectoryService {

    public ArrayList<Directory> listAllAvailableArtifactsDesc(@NonNull String artifactsBaseDirectory) {
        return (ArrayList<Directory>) Arrays.stream(Objects.requireNonNull(new File(artifactsBaseDirectory).listFiles()))
                .filter(File::isDirectory)
                .map(f -> new Directory(f.getName()))
                .sorted((d1, d2) -> -1 * d1.getName().compareTo(d2.getName()))
                .collect(Collectors.toList());
    }

    public ArrayList<Screenshot> listAllScreenshots(@NonNull String screenshotDir) {
        return (ArrayList<Screenshot>) Arrays.stream(Objects.requireNonNull(new File(screenshotDir).listFiles()))
                .filter(f -> f.isFile() && FileExtensionEnum.isImage(f.getName()))
                .map(f -> new Screenshot(f.getName(), new Date(f.lastModified()).toString()))
                .sorted((s1, s2) -> -1 * s1.getCreatedOn().compareTo(s2.getCreatedOn()))
                .collect(Collectors.toList());
    }

    public boolean deleteDirectory(@NonNull File path) {
        if (path.isFile()) {
            return path.delete();
        } else if (path.isDirectory() && Objects.requireNonNull(path.listFiles()).length == 0) {
            return path.delete();
        } else {
            boolean deleted = true;
            // Delete files and folders from current directory
            for (File f: Objects.requireNonNull(path.listFiles())) {
                deleted = deleted && deleteDirectory(f);
            }
            // Delete current directory
            return deleted && path.delete();
        }
    }

    public boolean deleteDirectoryIfEmpty(@NonNull File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length == 0) {
                return dir.delete();
            }
        } else {
            System.out.println("ERROR: Not a directory: " + dir);
        }
        return false;
    }
}
