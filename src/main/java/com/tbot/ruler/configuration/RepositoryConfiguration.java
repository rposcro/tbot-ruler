package com.tbot.ruler.configuration;

import com.tbot.ruler.persistance.json.JsonFileActuatorsRepository;
import com.tbot.ruler.persistance.json.JsonFilePluginsRepository;
import com.tbot.ruler.persistance.json.JsonFileRepositoryReader;
import com.tbot.ruler.persistance.json.JsonFileThingsRepository;
import com.tbot.ruler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RepositoryConfiguration {

    @Value("${ruler.thingsConfig.path}")
    private String configPath;

    @Autowired
    private FileUtil fileUtil;

    @Bean
    public JsonFileRepositoryReader repositoryReader() {
        return new JsonFileRepositoryReader(configPath, fileUtil);
    }

    @Bean
    public JsonFileActuatorsRepository actuatorsRepository(JsonFileRepositoryReader repositoryReader) {
        return JsonFileActuatorsRepository.builder()
                .repositoryReader(repositoryReader)
                .build();
    }

    @Bean
    public JsonFileThingsRepository thingsRepository(JsonFileRepositoryReader repositoryReader, JsonFileActuatorsRepository actuatorsRepository) {
        return JsonFileThingsRepository.builder()
                .repositoryReader(repositoryReader)
                .actuatorsRepository(actuatorsRepository)
                .build();
    }

    @Bean
    public JsonFilePluginsRepository pluginsRepository(JsonFileRepositoryReader repositoryReader, JsonFileThingsRepository thingsRepository) {
        return JsonFilePluginsRepository.builder()
                .repositoryReader(repositoryReader)
                .thingsRepository(thingsRepository)
                .build();
    }
}
