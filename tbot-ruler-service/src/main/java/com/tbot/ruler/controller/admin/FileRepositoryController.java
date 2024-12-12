package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.service.dump.DumpJsonService;
import com.tbot.ruler.service.dump.DumpJsonZipService;
import com.tbot.ruler.service.dump.JsonRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/admin/files")
public class FileRepositoryController extends AbstractController {

    @Autowired
    private DumpJsonService dumpJsonService;

    @Autowired
    private DumpJsonZipService dumpJsonZipService;

    @Autowired
    private JsonRepositoryService jsonRepositoryService;

    @GetMapping(value = "/dump/zip", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Resource> getDumpJsonZip() {
        byte[] dumpFile = dumpJsonZipService.dumpToJsonZip();

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"tbot-configuration-" + new Date().toString().replaceAll("\\s", "-") + ".zip\"")
            .contentLength(dumpFile.length)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(new ByteArrayResource(dumpFile));
    }

    @PutMapping("/dump/json")
    public ResponseEntity<String> dumpDataToJson() {
        dumpJsonService.dumpToJson();
        return ok(null);
    }

    @PutMapping("/dump/sql")
    public ResponseEntity<String> dumpDataToSql() {
        return ok(null);
    }

    @PutMapping("/load/json")
    public ResponseEntity<String> loadJsonRepository() {
        jsonRepositoryService.loadJsonRepository();
        return ok(null);
    }
}
