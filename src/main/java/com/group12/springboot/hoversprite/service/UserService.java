package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.UserCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.UserUpdateRequest;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User createUser(UserCreationRequest request){
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email has already been used.");
        }

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUserById(String userId){
        return userRepository.findById(userId)
                             .orElseThrow(() -> new RuntimeException("User Not Found."));
    }

    public User updateUser(String userId, UserUpdateRequest request){
        User user = getUserById(userId);

        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        return userRepository.save(user);
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
}
