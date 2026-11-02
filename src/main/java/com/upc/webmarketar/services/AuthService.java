package com.upc.webmarketar.services;

import com.upc.webmarketar.security.dtos.LoginDTO;
import com.upc.webmarketar.security.dtos.LoginRequest;
import com.upc.webmarketar.security.dtos.RegisterRequest;
import com.upc.webmarketar.security.dtos.UserDTO;

public interface AuthService {
    UserDTO register(RegisterRequest r);

    LoginDTO login(LoginRequest input);
}
