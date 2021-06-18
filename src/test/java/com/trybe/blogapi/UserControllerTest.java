package com.trybe.blogapi;


import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.resources.UserController;
import com.trybe.blogapi.services.JwtService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição com a senha com o tamanho menor de 6.")
    public void test01() {
        String json = "{" +
                "\"displayName\": \"Wesley Henrique\"," +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"12345\"," +
                "\"image\": \"wesley.png\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("\"password\" lenght must be 6 characteres long")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição com o displayName com tamanho menor do que 8.")
    public void test02() {
        String json = "{" +
                "\"displayName\": \"Wesley\"," +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"123456\"," +
                "\"image\": \"wesley.png\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("\"displayName\" lengh must be at least 8 characters long")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar CONFLICT ao tentar salvar um usuário que já existe.")
    public void test03() {
        String json = "{" +
                "\"displayName\": \"Wesley Henrique\"," +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"123456\"," +
                "\"image\": \"wesley.png\"" +
                "}";

        when(this.userRepository.existsByEmail(anyString())).thenReturn(true);

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Usuário já existe")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar CREATED com o Token.")
    public void test04() {
        String json = "{" +
                "\"displayName\": \"Wesley Henrique\"," +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"123456\"," +
                "\"image\": \"wesley.png\"" +
                "}";

        when(this.userRepository.existsByEmail(anyString())).thenReturn(false);
        when(this.userRepository.save(any())).thenReturn(new User(1L, "wesley@gmail.com"));

        this.mockMvc
                .perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar OK com o usuário.")
    public void test05() {
        User user = User
                        .builder()
                        .id(1L)
                        .displayName("Wesley Henrique")
                        .email("wesley@gmail.com")
                        .image("blabla.png")
                        .build();

        doNothing().when(this.jwtService).validaToken(anyString());
        when(this.userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        this.mockMvc
                .perform(
                        get("/user/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.displayName", is("Wesley Henrique")))
                .andExpect(jsonPath("$.email", is("wesley@gmail.com")))
                .andExpect(jsonPath("$.image", is("blabla.png")));
    }
}
