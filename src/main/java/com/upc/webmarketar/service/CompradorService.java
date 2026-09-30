package com.upc.webmarketar.service;

import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.entities.Vendedor;
import com.upc.webmarketar.repositories.CompradorRepository;
import com.upc.webmarketar.repositories.VendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompradorService {
    @Autowired
    private CompradorRepository compradorRepository;

    public List<Comprador> ListarCompradores() {
        return compradorRepository.findAll();
    }
}
