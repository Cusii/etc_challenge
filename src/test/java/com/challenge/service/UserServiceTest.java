package com.challenge.service;

import com.challenge.entity.UserEntity;
import com.challenge.exception.UserAlreadyExistsException;
import com.challenge.model.UserRegisterDTO;
import com.challenge.repository.UserRepository;
import com.challenge.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private UserService userService;

    private UserRegisterDTO userRegisterDTO;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRegisterDTO = new UserRegisterDTO("username", "1234-5678", "25", "M", "password");
        user = new UserEntity();
        user.setUserName("username");
        user.setUserPassword("encryptedPassword");
    }

//    @Test
//    void testUserRegister_Success() throws Exception {
//        when(userRepository.findByName(anyString())).thenReturn(Optional.empty());
//        when(encryptionUtil.encrypt(anyString())).thenReturn("encryptedPassword");
//        //when(userRepository.persist(any(Users.class))).thenReturn(user);
//
//        Users createdUser = userService.userRegister(userRegisterDTO);
//
//        assertNotNull(createdUser);
//        assertEquals("username", createdUser.getUserName());
//        verify(userRepository, times(1)).persist(any(Users.class));
//    }

    @Test
    void testUserRegister_UserAlreadyExists() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));

        UserAlreadyExistsException thrown = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.userRegister(userRegisterDTO);
        });

        assertEquals("El usuario username ya existe.", thrown.getMessage());
        verify(userRepository, times(0)).persist(any(UserEntity.class));
    }

//    @Test
//    void testValidateUserPassword_Success() throws Exception {
//        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));
//        when(encryptionUtil.decrypt(anyString())).thenReturn("password");
//
//        boolean isValid = userService.validateUserPassword("username", "password");
//
//        assertTrue(isValid);
//    }

//    @Test
//    void testValidateUserPassword_Failure() throws Exception {
//        when(userRepository.findByName(anyString())).thenReturn(Optional.of(user));
//        when(encryptionUtil.decrypt(anyString())).thenThrow(new RuntimeException("Decryption error"));
//
//        boolean isValid = userService.validateUserPassword("username", "password");
//
//        assertFalse(isValid);
//    }

    @Test
    void testValidateUserPassword_UserNotFound() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.empty());

        boolean isValid = userService.validateUserPassword("username", "password");

        assertFalse(isValid);
    }
}
