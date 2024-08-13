package com.challenge.service;

import com.challenge.entity.UserEntity;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.mapper.UserMapper;
import com.challenge.model.UserRegisterDTO;
import com.challenge.model.UserResponseDTO;
import com.challenge.repository.UserRepository;
import com.challenge.util.EncryptionUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@ApplicationScoped
public class UserService {

    private static final UserMapper mapper = UserMapper.INSTANCE;

    @Inject
    UserRepository userRepository;

    @Transactional
    public UserEntity userRegister(UserRegisterDTO userRegisterDTO) {
        Optional<UserEntity> userName = userRepository.findByName(userRegisterDTO.getUserName());
        if (userName.isPresent()) {
            throw new UserAlreadyExistsException("El usuario " + userRegisterDTO.getUserName() + " ya existe.");
        }
        UserEntity user = mapper.toEntity(userRegisterDTO);
        user.setUserPassword(encryptPassword(userRegisterDTO.getPassword()));
        userRepository.persist(user);
        return user;
    }

    public UserResponseDTO getUserById(Long id) throws Exception {
        UserEntity userEntity = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new Exception("Error al obtener el usuario"));
        return mapper.toResponseDTO(userEntity);
    }

    public boolean validateUserPassword(String userName, String plainPassword) {
        Optional<UserEntity> userOptional = userRepository.findByName(userName);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
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
