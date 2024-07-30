package com.challenge.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterDTO {
    private String userName;
    private String phone;
    private Integer age;
    private String gender;
    private String password;
}
