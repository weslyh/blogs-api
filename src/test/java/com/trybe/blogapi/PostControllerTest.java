package com.trybe.blogapi;

import com.trybe.blogapi.entities.BlogPost;
import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.exceptions.TokenException;
import com.trybe.blogapi.repositories.BlogPostRepository;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.resources.PostController;
import com.trybe.blogapi.services.JwtService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BlogPostRepository blogPostRepository;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição sem o campo title.")
    public void test01() {
        String json = "{" +
                "\"content\": \"Algum texto\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar BAD REQUEST ao tentar mandar requisição campo title com o texto vazio.")
    public void test02() {
        String json = "{" +
                "\"title\": \"\"," +
                "\"content\": \"Algum texto\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("\"title\" is not allowed to be empty")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve UNAUTHORIZED ao tentar mandar requisição sem token.")
    public void test03() {
        String json = "{" +
                "\"title\": \"Algum title\"," +
                "\"content\": \"Algum content\"" +
                "}";

        this.mockMvc
                .perform(
                        post("/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Token não encontrado")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar UNAUTHORIZED ao tentar mandar requisição com token inválido.")
    public void test04() {
        String json = "{" +
                "\"title\": \"Algum title\"," +
                "\"content\": \"Algum content\"" +
                "}";

        doThrow(new TokenException("Token expirado ou inválido")).when(this.jwtService).validaToken("Some Token");

        this.mockMvc
                .perform(
                        post("/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Token expirado ou inválido")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar CREATED com o campos json na resposta.")
    public void test05() {
        String json = "{" +
                "\"title\": \"Algum title\"," +
                "\"content\": \"Algum content\"" +
                "}";

        User user = new User(1L);
        BlogPost blogPost = new BlogPost("Algum title", "Algum content", user);

        doNothing().when(this.jwtService).validaToken(anyString());
        when(this.jwtService.getEmailFromToken(anyString())).thenReturn("token");
        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(this.blogPostRepository.save(any())).thenReturn(blogPost);

        this.mockMvc
                .perform(
                        post("/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Algum title")))
                .andExpect(jsonPath("$.content", is("Algum content")))
                .andExpect(jsonPath("$.userId", is(1)));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar NOTFOUND por não ter encontrado um post pelo id.")
    public void test06() {
        doNothing().when(this.jwtService).validaToken(anyString());
        when(this.blogPostRepository.findById(anyLong())).thenReturn(Optional.empty());

        this.mockMvc
                .perform(
                        get("/post/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Post não existe")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar OK com o blog post encontrado.")
    public void test07() {
        BlogPost blogPost = BlogPost
                .builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .published(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .user(new User(1L))
                .build();

        doNothing().when(this.jwtService).validaToken(anyString());
        when(this.blogPostRepository.findById(anyLong())).thenReturn(Optional.of(blogPost));

        this.mockMvc
                .perform(
                        get("/post/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title")))
                .andExpect(jsonPath("$.content", is("Content")))
                .andExpect(jsonPath("$.published", is(blogPost.getPublished().toString())))
                .andExpect(jsonPath("$.updated", is(blogPost.getPublished().toString())))
                .andExpect(jsonPath("$.user.id", is(1)));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar UNAUTHORIZED ao tentar atualizar o post pois o usuário não é detentor do post.")
    public void test08() {
        String json = "{" +
                "\"title\": \"Algum title\"," +
                "\"content\": \"Algum content\"" +
                "}";

        BlogPost blogPost = BlogPost
                .builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .published(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .user(new User(1L, "wesley@gmail.com"))
                .build();

        doNothing().when(this.jwtService).validaToken(anyString());
        when(this.blogPostRepository.findById(anyLong())).thenReturn(Optional.of(blogPost));
        when(this.jwtService.getEmailFromToken(anyString())).thenReturn("henrique@gmail.com");

        this.mockMvc
                .perform(
                        put("/post/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Usuário não autorizado")));
    }

    @SneakyThrows
    @Test
    @DisplayName("Deve retornar OK com o blog post atualizado no json.")
    public void test09() {
        String json = "{" +
                "\"title\": \"Algum title\"," +
                "\"content\": \"Algum content\"" +
                "}";

        BlogPost blogPost = BlogPost
                .builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .published(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .user(new User(1L, "wesley@gmail.com"))
                .build();

        doNothing().when(this.jwtService).validaToken(anyString());
        when(this.blogPostRepository.findById(anyLong())).thenReturn(Optional.of(blogPost));
        when(this.jwtService.getEmailFromToken(anyString())).thenReturn("wesley@gmail.com");
        when(this.blogPostRepository.save(any())).thenReturn(blogPost);

        this.mockMvc
                .perform(
                        put("/post/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .header("Authorization", "Some Token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Algum title")))
                .andExpect(jsonPath("$.content", is("Algum content")))
                .andExpect(jsonPath("$.userId", is(1)));
    }
}
