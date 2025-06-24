package com.aplazo.bnpl.credit.api.credit_api.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aplazo.bnpl.credit.api.credit_api.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);

}