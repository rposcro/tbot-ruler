package com.tbot.ruler.service;

import com.tbot.ruler.exceptions.ServiceExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Properties;

@Slf4j
@Service
public class PersistenceService {

    @Value("${ruler.cache.path}")
    private String directoryPath;

    private Properties properties;
    private Path propertiesFile;

    @PostConstruct
    public void initialize() {
        propertiesFile = Paths.get(directoryPath, "states.properties");
        properties = new Properties();

        if (Files.exists(propertiesFile)) {
            try(
                InputStream inputStream = Files.newInputStream(propertiesFile,
                    StandardOpenOption.CREATE, StandardOpenOption.READ)
            ) {
                properties.load(inputStream);
            } catch(IOException e) {
                throw new ServiceExecutionException("Could not open persistence file", e);
            }
        }
    }

    public void persist(String itemId, String key, String value) {
        properties.setProperty(key(itemId, key), value);
    }

    public Optional<String> retrieve(String itemId, String key) {
        return Optional.ofNullable(properties.getProperty(key(itemId, key)));
    }

    public void flush() {
        try (
            OutputStream outputStream = Files.newOutputStream(propertiesFile,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            properties.store(outputStream, "");
        } catch(IOException e) {
            throw new ServiceExecutionException("Could not write to file, persistence log not flushed!");
        }
    }

    private String key(String itemId, String key) {
        return itemId + "." + key;
    }
}
