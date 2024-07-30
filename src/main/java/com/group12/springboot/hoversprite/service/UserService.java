package com.group12.springboot.hoversprite.service;

import com.group12.springboot.hoversprite.dataTransferObject.request.user.FarmerCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.user.ReceptionistCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.user.SprayerCreationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.user.UserUpdateRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.ListResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.UserResponse;
import com.group12.springboot.hoversprite.entity.Role;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.entity.enums.RoleType;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.repository.RoleRepository;
import com.group12.springboot.hoversprite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserResponse createFarmer(FarmerCreationRequest request){
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.FARMER.name()).orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse createReceptionist( ReceptionistCreationRequest request){
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.RECEPTIONIST.name()).orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse createSprayer(SprayerCreationRequest request){
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.SPRAYER.name()).orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(role);
        user.setExpertise(request.getExpertise());
        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ListResponse<UserResponse> getUsersByRole(int pageNo, int pageSize, String role){
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.findAllByRoleName(role, pageable);

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        ListResponse<UserResponse> listResponse = new ListResponse<>();
        listResponse.setContent(userResponses);
        listResponse.setPageNo(userPage.getNumber());
        listResponse.setPageSize(userPage.getSize());
        listResponse.setTotalPages(userPage.getTotalPages());
        listResponse.setTotalSize(userPage.getTotalElements());
        listResponse.setLast(userPage.isLast());

        return listResponse;
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserById(Long userId){
        User user = userRepository.findById(userId)
                             .orElseThrow(() -> new RuntimeException("User Not Found."));
        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserByPhone(String phoneNumber){
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User Not Found."));
        return new UserResponse(user);
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponse getMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        String email = jwt.getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));
        return new UserResponse(user);
    }

    public UserResponse updateUser(Long userId, UserUpdateRequest request) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean authorize =  user.getEmail().equals(currentUsername) ||
                authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_RECEPTIONIST"));

        if(!authorize){
            throw new AccessDeniedException("You do not have permission to update this user.");
        }

        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
