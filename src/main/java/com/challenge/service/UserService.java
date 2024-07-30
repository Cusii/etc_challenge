package com.challenge.service;

import com.challenge.entity.Users;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.mapper.UserMapper;
import com.challenge.model.UserRegisterDTO;
import com.challenge.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Inject
    UserRepository userRepository;

    @Transactional
    public Users userRegister(UserRegisterDTO userRegisterDTO) {

        Optional<Users> userName = userRepository.findByName(userRegisterDTO.getUserName());
        if (userName.isPresent()) {
            String errorMessage = "User with username " + userRegisterDTO.getUserName() + " already exists.";
            LOG.error(errorMessage);
            throw new UserAlreadyExistsException(errorMessage);
        }
        Users user = mapper.toEntity(userRegisterDTO);
        user.setUserPassword(encryptPassword(userRegisterDTO.getPassword()));
        userRepository.persist(user);
        LOG.debug("User created with ID: {}", user.getUserId());
        return user;
    }

    public boolean validateUserPassword(String userName, String plainPassword) {
        Optional<Users> userOptional = userRepository.findByName(userName);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return BCrypt.checkpw(plainPassword, user.getUserPassword());
        }
        return false;
    }

    private String encryptPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
