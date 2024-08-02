package com.group12.springboot.hoversprite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.group12.springboot.hoversprite.dataTransferObject.request.UserCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.UserUpdateRequest;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User createUser(UserCreationRequest request){
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email has already been used.");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long userId){
        return userRepository.findById(userId)
                             .orElseThrow(() -> new RuntimeException("User Not Found."));
    }

    public User updateUser(Long userId, UserUpdateRequest request){
        User user = getUserById(userId);

        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        return userRepository.save(user);
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }
}
