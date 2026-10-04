package com.upc.webmarketar.controllers;

import com.upc.webmarketar.entities.Prototipoia;
import com.upc.webmarketar.services.PrototipoIAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class PrototipoIAController {
    @Autowired
    private PrototipoIAService prototipoIAService;

    //Api para ver los diseños por idComprador
    @GetMapping("/prototipoia/{idComprador}")
    public ResponseEntity<List<Prototipoia>> ListarPrototipoIAByComprador(@PathVariable Long idComprador){
        List<Prototipoia> prototipos = prototipoIAService.ListarPrototipoIAByCompardor(idComprador);
        return ResponseEntity.ok(prototipos);
    }

}
