package com.tbot.ruler.controller.misc;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.service.dump.DumpJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/misc")
public class MiscellaneousController extends AbstractController {

    @Autowired
    private DumpJsonService dumpJsonService;

    @PutMapping("/dump/json")
    public ResponseEntity<String> dumpDataToJson() {
        dumpJsonService.dumpToJson();
        return ok(null);
    }

    @PutMapping("/dump/sql")
    public ResponseEntity<String> dumpDataToSql() {
        return ok(null);
    }

}
