package com.trybe.blogapi.resources;

import com.trybe.blogapi.entities.requests.AuthenticationRequest;
import com.trybe.blogapi.entities.responses.TokenResponse;
import com.trybe.blogapi.exceptions.LoginException;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Log4j2
public class LoginController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return this.userRepository
                .findByEmail(authenticationRequest.getEmail())
                .filter(user -> this.passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))
                .map(user -> new TokenResponse(this.jwtService.geraToken(user.getEmail())))
                .orElseThrow(() -> new LoginException("Campos Inválidos !"));
    }
}
