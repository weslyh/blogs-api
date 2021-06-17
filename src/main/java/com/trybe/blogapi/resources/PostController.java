package com.trybe.blogapi.resources;

import com.trybe.blogapi.entities.BlogPost;
import com.trybe.blogapi.entities.User;
import com.trybe.blogapi.entities.dtos.BlogPostDTO;
import com.trybe.blogapi.entities.requests.BlogPostRequest;
import com.trybe.blogapi.entities.responses.BlogPostSaveResponse;
import com.trybe.blogapi.exceptions.NotFoundException;
import com.trybe.blogapi.exceptions.UsuarioSemPermissaoException;
import com.trybe.blogapi.repositories.BlogPostRepository;
import com.trybe.blogapi.repositories.UserRepository;
import com.trybe.blogapi.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public BlogPostSaveResponse save(@Valid @RequestBody BlogPostRequest blogPostRequest, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);
        Optional<User> user = this.userRepository.findByEmail(jwtService.getEmailFromToken(authorization));

        if (user.isPresent()) {
            BlogPost blogPost = new BlogPost(blogPostRequest.getTitle(), blogPostRequest.getContent());
            blogPost.setUser(user.get());
            BlogPost blogPostSaved = this.blogPostRepository.save(blogPost);

            return new BlogPostSaveResponse(blogPostSaved.getTitle(), blogPostSaved.getContent(), blogPostSaved.getUser().getId());
        } else {
            throw new NotFoundException("Usuário não econtrado !");
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BlogPostDTO> findAll(@RequestHeader String authorization) {
        jwtService.validaToken(authorization);
        return this.blogPostRepository
                .findAll()
                .stream()
                .map(BlogPost::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogPostDTO findById(@PathVariable(name = "id") Long id, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);

        return this.blogPostRepository.findById(id)
                .map(BlogPost::toDTO)
                .orElseThrow(() -> new NotFoundException("Post não existe"));
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogPostSaveResponse atualizaPost(@PathVariable(name = "id") Long id,
                                                    @RequestBody BlogPostRequest blogPostRequest,
                                                    @RequestHeader String authorization) {
        Optional<BlogPost> blogPost = this.blogPostRepository.findById(id);

        if (blogPost.isPresent()) {
            if (!blogPost.get().isUsuarioDetentorDoPost(this.jwtService.getEmailFromToken(authorization))) {
                throw new UsuarioSemPermissaoException("Usuário não autorizado");
            } else {
                blogPost.get().setTitle(blogPostRequest.getTitle());
                blogPost.get().setContent(blogPostRequest.getContent());

                return Optional.ofNullable(this.blogPostRepository.save(blogPost.get()))
                        .map(BlogPostSaveResponse::new)
                        .get();
            }
        } else {
            throw new NotFoundException("Post não existe");
        }
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<BlogPostDTO> searchPost(@RequestParam String q, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);
        List<BlogPost> blogs;

        if (q == null || q.isEmpty()) {
            blogs = this.blogPostRepository.findAll();
        } else {
            blogs = this.blogPostRepository.findAllByTitleOrContentContaining(q);
        }

        return blogs
                .stream()
                .map(BlogPost::toDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletaPost(@PathVariable(name = "id") Long id, @RequestHeader String authorization) {
        this.jwtService.validaToken(authorization);
        Optional<BlogPost> blogPost = this.blogPostRepository.findById(id);

        if (blogPost.isPresent()) {
            if (blogPost.get().isUsuarioDetentorDoPost(this.jwtService.getEmailFromToken(authorization))) {
                this.blogPostRepository.deleteById(id);
            } else {
                throw new UsuarioSemPermissaoException("Usuário não autorizado");
            }
        } else {
            throw new NotFoundException("Post não existe");
        }
    }
}
