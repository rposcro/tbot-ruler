package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.service.dump.DumpJsonService;
import com.tbot.ruler.service.dump.JsonRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/files")
public class FileRepositoryController extends AbstractController {

    @Autowired
    private DumpJsonService dumpJsonService;

    @Autowired
    private JsonRepositoryService jsonRepositoryService;

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
