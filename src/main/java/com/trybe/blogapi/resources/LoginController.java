package com.trybe.blogapi.resources;

import com.trybe.blogapi.entities.requests.UserRequest;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final JwtService jwtService;

    @PostMapping
    public void get(@RequestBody UserRequest userRequest) {
        //System.out.println(jwtService.geraToken(userRequest));
    }
}
