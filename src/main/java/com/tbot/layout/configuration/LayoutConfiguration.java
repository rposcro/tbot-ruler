package com.tbot.layout.configuration;

import com.tbot.layout.model.PanelLayout;
import com.tbot.ruler.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Configuration
public class LayoutConfiguration implements InitializingBean {

    @Value("${ruler.thingsConfig.path}")
    private String configPath;

    @Autowired
    private FileUtil fileUtil;

    private PanelLayout panelLayout;

    @Override
    public void afterPropertiesSet() {
        //panelLayout = fileUtil.deserializeJsonFile(new File(configPath + "/layout/layout.json"), PanelLayout.class);
    }

    @Bean
    public PanelLayout panelLayout() {
        return this.panelLayout;
    }
}
