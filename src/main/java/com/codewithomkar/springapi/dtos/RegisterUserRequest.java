package com.codewithomkar.springapi.dtos;

import com.codewithomkar.springapi.validators.LowerCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name is required")
    @Size(message = "Name must be less than 255 characters")
    String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    @LowerCase(message = "Email must be in lowercase")
    String email;

    @NotBlank
    @Size(min = 6, max = 25, message = "Password should be between 6 and 25 characters long")
    String password;
}
