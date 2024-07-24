package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.UserCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.UserUpdateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.UserResponse;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserResponse createUser(UserCreationRequest request){
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());
        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public List<UserResponse> getUsers(){
        List<User> users = userRepository.findAll();
        return  users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserById(String userId){
        User user = userRepository.findById(userId)
                             .orElseThrow(() -> new RuntimeException("User Not Found."));
        return new UserResponse(user);
    }
    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

        return new UserResponse(user);
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found."));

        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
