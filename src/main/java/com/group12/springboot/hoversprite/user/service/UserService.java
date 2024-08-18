package com.group12.springboot.hoversprite.user.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.common.RoleRepository;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.user.FarmerCreationRequest;
import com.group12.springboot.hoversprite.user.FarmerDTO;
import com.group12.springboot.hoversprite.user.ReceptionistCreationRequest;
import com.group12.springboot.hoversprite.user.ReceptionistDTO;
import com.group12.springboot.hoversprite.user.SprayerCreationRequest;
import com.group12.springboot.hoversprite.user.UserAPI;
import com.group12.springboot.hoversprite.user.UserAuthenticateDTO;
import com.group12.springboot.hoversprite.user.UserResponse;
import com.group12.springboot.hoversprite.user.UserUpdateRequest;
import com.group12.springboot.hoversprite.user.entity.User;
import com.group12.springboot.hoversprite.user.enums.RoleType;
import com.group12.springboot.hoversprite.user.repository.UserRepository;

@Service
@Transactional
public class UserService implements UserAPI {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserResponse createFarmer(FarmerCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.FARMER.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse createReceptionist(ReceptionistCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.RECEPTIONIST.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse createSprayer(SprayerCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.SPRAYER.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

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

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ListResponse<UserResponse> getUsersByRole(int pageNo, int pageSize, String role) {
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

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found."));
        return new UserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserByPhone(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User Not Found."));
        return new UserResponse(user);
    }

    @Override
    @PostAuthorize("returnObject.id == T(java.lang.Long).parseLong(principal.subject)")
    public UserResponse getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) jwtAuthToken.getPrincipal();
        Long userId = Long.parseLong(jwt.getSubject());
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        return new UserResponse(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean authorize = user.getEmail().equals(currentUsername) ||
                authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_RECEPTIONIST"));

        if (!authorize) {
            throw new AccessDeniedException("You do not have permission to update this user.");
        }

        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return new UserResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public FarmerDTO findFarmerById(Long farmerId) {
        Optional<User> user = userRepository.findById(farmerId);
        return user.map(FarmerDTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.FARMER_NOT_EXIST));
    }

    @Override
    public ReceptionistDTO findReceptionistById(Long receptionistId) {
        Optional<User> user = userRepository.findById(receptionistId);
        return user.map(ReceptionistDTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.RECEPTIONIST_NOT_EXIST));
    }

    @Override
    public UserAuthenticateDTO findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserAuthenticateDTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));
    }

    @Override
    public UserAuthenticateDTO findUserByPhoneNumber(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(UserAuthenticateDTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_NUMBER_NOT_EXISTS));
    }
}