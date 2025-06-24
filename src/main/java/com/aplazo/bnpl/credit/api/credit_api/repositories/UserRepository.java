package com.aplazo.bnpl.credit.api.credit_api.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aplazo.bnpl.credit.api.credit_api.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
