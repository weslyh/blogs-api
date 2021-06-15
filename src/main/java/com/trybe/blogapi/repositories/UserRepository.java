package com.trybe.blogapi.repositories;

import com.trybe.blogapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM User u WHERE u.email = ?1")
    void deleteByEmail(String email);
}
