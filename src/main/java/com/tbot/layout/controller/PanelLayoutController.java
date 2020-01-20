package com.tbot.layout.controller;

import com.tbot.layout.configuration.LayoutConfiguration;
import com.tbot.layout.model.PanelLayout;
import com.tbot.ruler.controller.AbstractController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = ControllerConstants.ENDPOINT_LAYOUT)
public class PanelLayoutController extends AbstractController {

    @Autowired
    private LayoutConfiguration layoutConfiguration;

    @GetMapping(value = "", produces = ControllerConstants.MEDIA_TYPE)
    public ResponseEntity<PanelLayout> getLayout() {
        return response(ResponseEntity.ok())
            .body(layoutConfiguration.panelLayout());
    }
}
