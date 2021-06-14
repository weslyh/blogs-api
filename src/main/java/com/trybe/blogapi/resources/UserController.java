package com.trybe.blogapi.resources;

import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.entities.dtos.UserDTO;
import com.trybe.blogapi.entities.requests.UserRequest;
import com.trybe.blogapi.entities.responses.ErrorResponse;
import com.trybe.blogapi.entities.responses.TokenResponse;
import com.trybe.blogapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private ModelMapper modelMapper = new ModelMapper();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> post(@Valid @RequestBody UserRequest userRequest) {
        if (this.userRepository.existsByEmail(userRequest.getEmail())) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Usuário já existe"));
        }

        User user = this.modelMapper.map(userRequest, User.class);

        if (this.userRepository.save(user) != null) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new TokenResponse());
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping
    public ResponseEntity<Set<UserDTO>> get() {
        Set<UserDTO> users = this.userRepository.findAll()
                .stream()
                .map(user -> this.modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(users);
    }
}
