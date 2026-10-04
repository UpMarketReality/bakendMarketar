package com.upc.webmarketar.controllers;

import com.upc.webmarketar.entities.Vendedor;
import com.upc.webmarketar.service.VendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class VendedorController {
    @Autowired
    private VendedorService vendedorService;

    @GetMapping("/vendedor")
    public List<Vendedor> ListarVendedores() {
        return vendedorService.ListarVendedores();
    }
}
