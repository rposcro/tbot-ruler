package com.tbot.ruler.controller;

import com.tbot.ruler.broker.payload.ApplicationInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AboutController extends AbstractController {
    
    public static final String NOT_AVAILABLE = "Not Available";
    
    private ApplicationInfo appInfo;
    
    @Autowired
    public AboutController(@Value("${spring.application.name}") String appName) {
        final String appVersion = retrieveAppVersion();
        this.appInfo = new ApplicationInfo(
                appName != null ? appName : NOT_AVAILABLE,
                appVersion != null ? appVersion : NOT_AVAILABLE);
    }
    
    @RequestMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> home(HttpServletRequest request) {
        return ok(String.format("Welcome.\nApplication name: %s\nVersion: %s\nYour IP: %s\n",
                appInfo.getAppName(),
                appInfo.getAppVersion(),
                "" + request.getRemoteAddr()));
    }
    
    String retrieveAppVersion() {
        return AboutController.class.getPackage().getImplementationVersion();
    }
}
