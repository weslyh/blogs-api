package com.trybe.blogapi.resources;

import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.entities.requests.AuthenticationRequest;
import com.trybe.blogapi.entities.responses.TokenResponse;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return this.userRepository
                .findByEmail(authenticationRequest.getEmail())
                .filter(user -> this.passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword()))
                .map(user -> ResponseEntity.ok(new TokenResponse(this.jwtService.geraToken(user.getEmail()))))
                .orElseThrow(() -> new RuntimeException("Campos Inválidos !"));
    }
}
