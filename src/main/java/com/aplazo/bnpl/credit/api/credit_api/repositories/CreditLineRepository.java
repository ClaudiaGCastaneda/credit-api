package com.aplazo.bnpl.credit.api.credit_api.repositories;

import com.aplazo.bnpl.credit.api.credit_api.entities.CreditLine;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface CreditLineRepository extends CrudRepository<CreditLine, Integer> {

    Optional<CreditLine> findFirstByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndActiveTrue(Integer ageForMinAge,
            Integer ageForMaxAge);

}
