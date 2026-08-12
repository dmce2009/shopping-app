package com.codewithomkar.springapi.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Email can not be blank")
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Email can not be blank")
    @Size(min = 6, max = 25, message = "Password should be between 6 and 25 characters long")
    private String password;
}
