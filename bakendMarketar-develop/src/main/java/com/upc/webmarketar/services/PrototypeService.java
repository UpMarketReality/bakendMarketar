package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.PageDTO;
import com.upc.webmarketar.dtos.PrototypeDTO;
import com.upc.webmarketar.dtos.PrototypeRequest;

public interface PrototypeService {
    PrototypeDTO own(long id);

    PageDTO<PrototypeDTO> list(Long category, int page, int size);

    PrototypeDTO generate(PrototypeRequest p);

    void authorizeImage(long prototypeId);
}
