package com.upc.webmarketar.service;

import com.upc.webmarketar.entities.Vendedor;
import com.upc.webmarketar.repositories.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendedorService {
    @Autowired
    private VendedorRepository vendedorRepository;

    public List<Vendedor> ListarVendedores() {
        return vendedorRepository.findAll();
    }
}
