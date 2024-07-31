package com.challenge.service;

import com.challenge.entity.Users;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.mapper.UserMapper;
import com.challenge.model.UserRegisterDTO;
import com.challenge.repository.UserRepository;
import com.challenge.util.EncryptionUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class UserService {

    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Inject
    UserRepository userRepository;

    @Transactional
    public Users userRegister(UserRegisterDTO userRegisterDTO) {
        Optional<Users> userName = userRepository.findByName(userRegisterDTO.getUserName());
        if (userName.isPresent()) {
            String errorMessage = "El usuario " + userRegisterDTO.getUserName() + " ya existe.";
            log.error(errorMessage);
            throw new UserAlreadyExistsException(errorMessage);
        }
        Users user = mapper.toEntity(userRegisterDTO);
        user.setUserPassword(encryptPassword(userRegisterDTO.getPassword()));
        userRepository.persist(user);
        log.debug("User created with ID: {}", user.getUserId());
        return user;
    }

    public boolean validateUserPassword(String userName, String plainPassword) {
        Optional<Users> userOptional = userRepository.findByName(userName);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            try {
                String decryptedPassword = EncryptionUtil.decrypt(user.getUserPassword());
                return plainPassword.equals(decryptedPassword);
            } catch (Exception e) {
                log.error("Error decrypting password", e);
                return false;
            }
        }
        return false;
    }

    private String encryptPassword(String plainPassword) {
        try {
            return EncryptionUtil.encrypt(plainPassword);
        } catch (Exception e) {
            log.error("Error encrypting password", e);
            throw new RuntimeException("Error encrypting password", e);
        }
    }
}
