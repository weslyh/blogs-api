package com.trybe.blogapi;


import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.resources.LoginController;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
public class LoginTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição sem o email.")
    public void test01() {
        String json = "{" +
                "\"password\": \"12345\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição sem o password.")
    public void test02() {
        String json = "{" +
                "\"email\": \"email@gmail.com\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição com email vazio.")
    public void test03() {
        String json = "{" +
                "\"email\": \"\"," +
                "\"password\": \"123456\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("\"email\" is not allowed to be empty")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição com password vazio.")
    public void test04() {
        String json = "{" +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição com login de usuário que não existe.")
    public void test05() {
        String json = "{" +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"123456\"" +
                "}";

        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição com a senha errada.")
    public void test06() {
        String json = "{" +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"1234567\"" +
                "}";

        User user = User
                .builder()
                .id(1L)
                .email("wesley@gmail.com")
                .password("$2y$12$35flE7znse2NOWLCdKms4e5w0bbfNdaVMKx0Dcii6CUk1927T46LG")
                .build();

        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar OK com o token.")
    public void test07() {
        String json = "{" +
                "\"email\": \"wesley@gmail.com\"," +
                "\"password\": \"123456\"" +
                "}";

        User user = User
                .builder()
                .id(1L)
                .email("wesley@gmail.com")
                .password("$2y$10$G8jYR82HZ0BfAuJHOobhgepaY81AQi3CYnWAaDb4dBMXGpzfHrgRu")
                .build();

        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(this.passwordEncoder.matches(anyString(), any())).thenReturn(true);

        this.mockMvc
                .perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk());
    }
}
