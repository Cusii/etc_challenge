package com.challenge.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDTO {
    private String userName;
    private String password;
}
