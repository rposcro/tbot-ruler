package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.SchemasRepository;
import com.tbot.ruler.persistance.model.SchemaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@RestController
@RequestMapping("/admin/schemas")
public class SchemasAdminController extends AbstractController {

    @Autowired
    private SchemasRepository schemasRepository;

    @GetMapping("/owners/{owner}/types/{type}")
    public ResponseEntity<SchemaEntity> getSchema(@PathVariable("owner") String owner, @PathVariable("type") String type) {
        SchemaEntity schemaEntity = schemasRepository.findByOwnerAndType(owner, type)
            .orElseThrow(() -> new ServiceRequestException(format("Schema %s-%s not found!", owner, type)));
        return ok(schemaEntity);
    }
}
