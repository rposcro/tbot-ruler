package com.tbot.ruler.controller.admin;

import com.tbot.ruler.controller.AbstractController;
import com.tbot.ruler.exceptions.ServiceRequestException;
import com.tbot.ruler.persistance.StencilsRepository;
import com.tbot.ruler.persistance.model.StencilEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

@RestController
@RequestMapping("/admin/stencils")
public class StencilsAdminController extends AbstractController {

    @Autowired
    private StencilsRepository stencilsRepository;

    @GetMapping("/owners/{owner}/types/{type}")
    public ResponseEntity<StencilEntity> getStencil(@PathVariable("owner") String owner, @PathVariable("type") String type) {
        StencilEntity stencilEntity = stencilsRepository.findByOwnerAndType(owner, type)
            .orElseThrow(() -> new ServiceRequestException(format("Stencil %s-%s not found!", owner, type)));
        return ok(stencilEntity);
    }
}
