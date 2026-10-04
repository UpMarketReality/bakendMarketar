package com.upc.webmarketar.services;

import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.repositories.CompradorRepository;
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
