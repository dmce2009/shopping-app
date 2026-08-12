package com.codewithomkar.springapi.controllers;

import com.codewithomkar.springapi.config.JwtConfig;
import com.codewithomkar.springapi.dtos.JWTResponseDTO;
import com.codewithomkar.springapi.dtos.LoginRequestDTO;
import com.codewithomkar.springapi.dtos.UserDto;
import com.codewithomkar.springapi.mappers.UserMapper;
import com.codewithomkar.springapi.repositories.UserRepository;
import com.codewithomkar.springapi.services.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jWTService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JWTResponseDTO> login(
            HttpServletResponse httpServletResponse,
            @Valid @RequestBody LoginRequestDTO loginRequestDTO
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );

        var userEntity  = userRepository.findByEmail(loginRequestDTO.getEmail()).orElse(null);
        if(userEntity == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String accessToken = jWTService.generateAccessToken(userEntity);
        String refreshToken = jWTService.generateRefreshToken(userEntity);


        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok(new JWTResponseDTO(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTResponseDTO> refresh(
            @CookieValue ("refreshToken" ) String refreshToken
    ){
        if(!jWTService.validateToken(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var userId = jWTService.getUserIdFromToken(refreshToken);
        var userEntity = userRepository.findById(userId).orElseThrow();
        var accessToken = jWTService.generateAccessToken(userEntity);

        return ResponseEntity.ok(new JWTResponseDTO(accessToken));

    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        Long userid = (Long) authentication.getPrincipal();

        var userEntity  = userRepository.findById(userid).orElse(null);
        if(userEntity == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var userdto = userMapper.toDto(userEntity);
        return ResponseEntity.ok(userdto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
