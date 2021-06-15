package com.trybe.blogapi.resources;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.entities.dtos.UserDTO;
import com.trybe.blogapi.entities.requests.UserRequest;
import com.trybe.blogapi.entities.responses.ErrorResponse;
import com.trybe.blogapi.entities.responses.TokenResponse;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody UserRequest userRequest) {
        if (this.userRepository.existsByEmail(userRequest.getEmail())) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Usuário já existe"));
        }

        User user = this.modelMapper.map(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("Password generated {}", user.getPassword());

        if (this.userRepository.save(user) != null) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new TokenResponse(this.jwtService.geraToken(user.getEmail())));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<Set<UserDTO>> findAll(@RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);

        Set<UserDTO> users = this.userRepository.findAll()
                .stream()
                .map(user -> this.modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") Long id, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);

        return this.userRepository.findById(id)
                .map(user -> ResponseEntity.ok(this.modelMapper.map(user, UserDTO.class)))
                .orElseThrow(() -> new RuntimeException(""));
    }

    @DeleteMapping("me")
    public ResponseEntity<?> delete(@RequestHeader String authorization) {
        DecodedJWT jwt = this.jwtService.decodeToken(authorization);
        String emailUsuarioAutenticado = jwt.getClaim("email").asString();
        this.userRepository.deleteByEmail(emailUsuarioAutenticado);

        return ResponseEntity.noContent().build();
    }
}
