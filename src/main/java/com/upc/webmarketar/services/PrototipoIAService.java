package com.upc.webmarketar.services;

import com.upc.webmarketar.entities.Prototipoia;
import com.upc.webmarketar.repositories.PrototipoIARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrototipoIAService {
    @Autowired
    private PrototipoIARepository prototipoIARepository;

    public List<Prototipoia> ListarPrototipoIAByCompardor(Long idComprador){
        return prototipoIARepository.findAllByComprador_Id(idComprador);
    }

}
