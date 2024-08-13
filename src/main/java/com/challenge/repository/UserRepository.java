package com.challenge.repository;

import com.challenge.entity.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {

    public Optional<UserEntity> findByName(String userName) {
        return find("userName", userName).firstResultOptional();
    }
}
