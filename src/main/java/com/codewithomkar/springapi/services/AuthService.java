package com.codewithomkar.springapi.services;

import com.codewithomkar.springapi.entities.User;
import com.codewithomkar.springapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null ) {
            return null;
        }
        Long userid = (Long) authentication.getPrincipal();

        return  userRepository.findById(userid).orElse(null);
    }
}
