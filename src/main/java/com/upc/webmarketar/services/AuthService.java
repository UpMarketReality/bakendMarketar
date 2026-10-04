package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.security.dtos.*;

public interface AuthService {
    UserDTO register(RegisterRequest r);

    LoginDTO login(LoginRequest input);
}
