package com.group12.springboot.hoversprite.user.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.group12.springboot.hoversprite.user.*;
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
    @Transactional(readOnly = false)
    public UserOAuth2DTO createOrUpdateUser(UserOAuth2DTO user) {
        Optional<User> existingAccount = userRepository.findByEmail(user.getEmail());
        if (existingAccount.isEmpty()) {
            Role role = roleRepository.findByName(RoleType.FARMER.name())
                    .orElseThrow(() -> new RuntimeException("User's Role Not Found."));
            user.setRole(role);

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

            User trueUser = new User();
            trueUser.setEmail(user.getEmail());
            trueUser.setFullName(user.getFullName());
            trueUser.setPhoneNumber(user.getPhoneNumber());
            trueUser.setRole(user.getRole());
            trueUser.setPassword(passwordEncoder.encode("Test!"));
            System.out.println(user.getEmail());
            System.out.println("save");
            userRepository.save(trueUser);
            System.out.println(user.getEmail());
            return user;
        }
//        existingAccount.setEmail(user.getEmail());
//        existingAccount.setFullName(user.getFullName());
//        existingAccount.setPhoneNumber(user.getPhoneNumber());
//        existingAccount.setRole(user.getRole());
//        userRepository.save(existingAccount);

        return existingAccount.map(UserOAuth2DTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.PHONE_NUMBER_NOT_EXISTS));
    }

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
    public FarmerDTO findFarmerByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(FarmerDTO::new)
                .orElseThrow(() -> new CustomException(ErrorCode.FARMER_NOT_EXIST));
    }

    @Override
    public FarmerDTO findFarmerByPhoneNumber(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
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