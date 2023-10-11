package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.service.dump.DumpJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dump")
public class DumpController extends AbstractController {

    @Autowired
    private DumpJsonService dumpJsonService;

    @PutMapping("/json")
    public ResponseEntity<String> dumpDataToJson() {
        dumpJsonService.dumpToJson();
        return ok(null);
    }

    @PutMapping("/sql")
    public ResponseEntity<String> dumpDataToSql() {
        return ok(null);
    }

}
