package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.security.dtos.*;

public interface PrototypeService {
    PrototypeDTO own(long id);

    PageDTO<PrototypeDTO> list(Long category, int page, int size);

    PrototypeDTO generate(PrototypeRequest p);

    void authorizeImage(long prototypeId);
}