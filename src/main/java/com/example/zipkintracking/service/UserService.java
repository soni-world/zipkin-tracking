package com.example.zipkintracking.service;

import com.example.zipkintracking.entity.User;
import com.example.zipkintracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<User> getAllUsers() {
        log.info("Fetching all users from database");
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getEmail());
        return userRepository.save(user);
    }
    
    public Optional<User> updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setCity(userDetails.getCity());
                    return userRepository.save(user);
                });
    }
    
    public boolean deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}

