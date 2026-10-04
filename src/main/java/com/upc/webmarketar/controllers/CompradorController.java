package com.upc.webmarketar.controllers;

import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.services.CompradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class CompradorController {
    @Autowired
    private CompradorService compradorService;

    @GetMapping("/comprador")
    public List<Comprador>  ListarCompradores() {
        return compradorService.ListarCompradores();
    }
}
