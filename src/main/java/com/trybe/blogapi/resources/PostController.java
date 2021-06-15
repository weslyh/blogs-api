package com.trybe.blogapi.resources;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.trybe.blogapi.entities.BlogPost;
import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.entities.dtos.BlogPostDTO;
import com.trybe.blogapi.entities.requests.BlogPostRequest;
import com.trybe.blogapi.entities.responses.BlogPostSaveResponse;
import com.trybe.blogapi.repositories.BlogPostRepository;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;

    @PostMapping
    public ResponseEntity<BlogPostSaveResponse> save(@Valid @RequestBody BlogPostRequest blogPostRequest, @RequestHeader String authorization) {
        DecodedJWT jwt = jwtService.decodeToken(authorization);
        String email = jwt.getClaim("email").asString();

        Optional<User> user = this.userRepository.findByEmail(email);

        if (user.isPresent()) {
            BlogPost blogPost = new BlogPost(blogPostRequest.getTitle(), blogPostRequest.getContent());
            blogPost.setUser(user.get());

            BlogPost blogPostSaved = this.blogPostRepository.save(blogPost);
            BlogPostSaveResponse blogPostSaveResponse =
                    new BlogPostSaveResponse(blogPostSaved.getTitle(), blogPostSaved.getContent(), blogPostSaved.getUser().getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(blogPostSaveResponse);
        } else {
            throw new RuntimeException("Token expirado !");
        }
    }

    @GetMapping
    public ResponseEntity<List<BlogPostDTO>> findAll(@RequestHeader String authorization) {
        jwtService.validaToken(authorization);

        List<BlogPostDTO> blogs = this.blogPostRepository.findAll()
                .stream()
                .map(BlogPost::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(blogs);
    }

    @GetMapping("{id}")
    public ResponseEntity<BlogPostDTO> findById(@PathVariable(name = "id") Long id, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);

        return this.blogPostRepository.findById(id)
                .map(BlogPost::toDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Not Found"));
    }

    @PutMapping("{id}")
    public ResponseEntity<BlogPostDTO> atualizaPost(@PathVariable(name = "id") Long id,
                                                    @RequestBody BlogPostRequest blogPostRequest,
                                                    @RequestHeader String authorization) {
        DecodedJWT jwt = this.jwtService.decodeToken(authorization);
        String email = jwt.getClaim("email").asString();

        Optional<BlogPost> blogPost = this.blogPostRepository.findById(id);

        if (blogPost.isPresent()) {
            if (!blogPost.get().getUser().getEmail().equals(email)) {
                throw new RuntimeException("Usuário não tem permissão para editar o post.");
            } else {
                blogPost.get().setTitle(blogPostRequest.getTitle());
                blogPost.get().setContent(blogPostRequest.getContent());

                return ResponseEntity.ok(this.blogPostRepository.save(blogPost.get()).toDTO());
            }
        } else {
            throw new RuntimeException("Blog post não existe !");
        }
    }
}
