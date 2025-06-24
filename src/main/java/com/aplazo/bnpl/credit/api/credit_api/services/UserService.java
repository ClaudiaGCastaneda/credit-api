package com.aplazo.bnpl.credit.api.credit_api.services;

import java.util.List;

import com.aplazo.bnpl.credit.api.credit_api.entities.User;

public interface UserService {

    List<User> findAll();

    User save(User user);

    boolean existsByUsername(String username);
}
