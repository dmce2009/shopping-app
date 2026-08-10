package com.codewithomkar.springapi.controllers;

import com.codewithomkar.springapi.dtos.ChangePasswordRequest;
import com.codewithomkar.springapi.dtos.RegisterUserRequest;
import com.codewithomkar.springapi.dtos.UpdateUserRequest;
import com.codewithomkar.springapi.dtos.UserDto;
import com.codewithomkar.springapi.entities.User;
import com.codewithomkar.springapi.mappers.UserMapper;
import com.codewithomkar.springapi.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @GetMapping()
    public Iterable<UserDto> getAllUsers(
//            @RequestHeader(name = "x-auth-token" ,required = false) String authToken,
            @RequestParam(name = "sort", required = false, defaultValue = "")
            String sortBy) {
        if (!Set.of("name", "email").contains(sortBy)) {
            sortBy = "name";
        }
        var usersList = userRepository.findAll(Sort.by(sortBy));
        var userDtoList = usersList.stream().map(userMapper::toDto).toList();

        return userDtoList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {

        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    public ResponseEntity<?> registerUser(
            UriComponentsBuilder uriBuilder,
            @Valid @RequestBody RegisterUserRequest createUserRequest) {

        boolean isEmailExist = userRepository.existsByEmail(createUserRequest.getEmail());
        if (isEmailExist) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email already registered. Please provide a different email address."));
        }
        System.out.println(userMapper.toEntity(createUserRequest));
        User user = userRepository.save(userMapper.toEntity(createUserRequest));

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }

    @PutMapping("{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateUserRequest updateUserRequest
    ) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(updateUserRequest, user);

        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();

        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            @PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (!user.getPassword().equals(changePasswordRequest.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        user.setPassword(changePasswordRequest.getNewPassword());
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }


}
