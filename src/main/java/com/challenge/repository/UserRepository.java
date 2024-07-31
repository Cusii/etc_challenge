package com.challenge.repository;

import com.challenge.entity.Users;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<Users> {

    public Optional<Users> findByName(String userName) {
        return find("userName", userName).firstResultOptional();
    }
}
