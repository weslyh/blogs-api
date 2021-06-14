package com.trybe.blogapi.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String displayName;
    private String email;
    @ToString.Exclude
    private String password;
    private String image;

    @OneToMany(mappedBy = "user")
    private Set<Post> posts;
}
