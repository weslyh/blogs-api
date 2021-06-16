package com.trybe.blogapi.resources;

import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.entities.dtos.UserDTO;
import com.trybe.blogapi.entities.requests.UserRequest;
import com.trybe.blogapi.entities.responses.TokenResponse;
import com.trybe.blogapi.exceptions.NotFoundException;
import com.trybe.blogapi.exceptions.UsuarioJaExisteException;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse save(@Valid @RequestBody UserRequest userRequest) {
        if (this.userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UsuarioJaExisteException("Usuário já existe");
        }

        User user = this.modelMapper.map(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (this.userRepository.save(user) != null) {
            return new TokenResponse(this.jwtService.geraToken(user.getEmail()));
        } else {
            throw new RuntimeException("Ocorreu um erro desconhecido !");
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Set<UserDTO> findAll(@RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);
        return this.userRepository.findAll()
                .stream()
                .map(user -> this.modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toSet());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO findById(@PathVariable(name = "id") Long id, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);

        return this.userRepository.findById(id)
                .map(user -> this.modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NotFoundException("Usuário não existe"));
    }

    @DeleteMapping("me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader String authorization) {
        this.userRepository.deleteByEmail(jwtService.getEmailFromToken(authorization));
    }
}
