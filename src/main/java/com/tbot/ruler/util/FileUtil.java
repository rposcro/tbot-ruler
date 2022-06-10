package com.tbot.ruler.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.tbot.ruler.exceptions.RulerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileUtil {

    private static final int MAX_PACKAGE_SCANNING_DEPTH = 100;

    public List<File> findJsonsInPackage(String packagePath) {
        return findJsonsInSubPackages(packagePath, 1)
            .collect(Collectors.toList());
    }

    public List<File> findJsonsInSubPackages(String rootPath) {
        return findJsonsInSubPackages(rootPath, MAX_PACKAGE_SCANNING_DEPTH)
            .collect(Collectors.toList());
    }

    public <T> List<T> deserializeJsonFilesInPackage(String dirPath, Class<T> dtoClass) {
        return this.findJsonsInPackage(dirPath).stream()
            .map(file -> deserializeJsonFile(file, dtoClass))
            .collect(Collectors.toList());
    }

    public <T> List<T> deserializeJsonFilesInSubPackages(String rootPath, Class<T> dtoClass) {
        return this.findJsonsInSubPackages(rootPath).stream()
            .map(file -> deserializeJsonFile(file, dtoClass))
            .collect(Collectors.toList());
    }

    public <T> T deserializeJsonFile(File jsonFile, Class<T> dtoClass) {
        try {
            logFile(jsonFile);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Jdk8Module());
                return mapper.readValue(jsonFile, dtoClass);
        }
        catch(IOException e) {
            log.error(String.format("Failed to deserialize json: %s!", jsonFile.getAbsolutePath()), e);
            throw new RulerException(e);
        }
    }

    private Stream<File> findJsonsInSubPackages(String rootPath, int scanDepth) {
        try {
            File rootDir = new File(rootPath);
            log.info("Scanning package {} and subpackages with depth {}", rootDir.getAbsolutePath(), scanDepth);
            return Files.find(rootDir.toPath(), scanDepth, (path, attributes) -> attributes.isRegularFile() && path.toFile().getName().endsWith(".json"), FileVisitOption.FOLLOW_LINKS)
                .map(Path::toFile);
        } catch(IOException e) {
            throw new RulerException(e);
        }
    }

    private void logFile(File theFile) {
        StringBuffer content = new StringBuffer(theFile.getAbsolutePath()).append("\n");
        try (
            BufferedReader reader = new BufferedReader(new FileReader(theFile));
        ) {
            reader.lines()
                .forEach(line -> content.append(line).append("\n"));
        } catch(IOException e) {
                log.error(e.getMessage(), e);
        }
        log.info("Here the file: " + content.toString());
    }
}
