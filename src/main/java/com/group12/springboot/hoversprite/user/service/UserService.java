package com.group12.springboot.hoversprite.user.service;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import com.group12.springboot.hoversprite.user.*;
import jakarta.servlet.http.HttpServletRequest;

import com.group12.springboot.hoversprite.config.CustomUserDetails;
import com.group12.springboot.hoversprite.email.EmailAPI;
import com.group12.springboot.hoversprite.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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
    private final EmailAPI emailAPI;

    public UserService(@Lazy EmailAPI emailAPI) {
        this.emailAPI = emailAPI;
    }

    public FarmerExternalSignUpInfoResponse receiveFarmerExternalInfo(HttpServletRequest request) {

        String token = JwtUtils.getJwtFromCookies(request);
        if (token != null && token.contains("#")) {
            token = token.split("#")[0]; // Remove the fragment part if present
        }

        try {
            // Initialize the JWT decoder with your signing key
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN".getBytes(), "HMACSHA512");
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

            // Decode the token
            Jwt jwt = jwtDecoder.decode(token);

            // Extract claims from the JWT
            String name = jwt.getClaimAsString("name");
            String email = jwt.getClaimAsString("email");

            // Return the response with the extracted information
            return new FarmerExternalSignUpInfoResponse(email, name);
        } catch (JwtException e) {
            // Handle the case where the token is invalid
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public UserResponse createFarmerExternal(FarmerExternalCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        if (userRepository.existsByPhoneNumber(processPhoneNumber(request.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.PHONE_NUMBER_USED);
        }

        Role role = roleRepository.findByName(RoleType.FARMER.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(processPhoneNumber(request.getPhoneNumber()));
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    public UserResponse createFarmer(FarmerCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        if (userRepository.existsByPhoneNumber(processPhoneNumber(request.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.PHONE_NUMBER_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.FARMER.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(processPhoneNumber(request.getPhoneNumber()));
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse createReceptionist(ReceptionistCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        if (userRepository.existsByPhoneNumber(processPhoneNumber(request.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.PHONE_NUMBER_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.RECEPTIONIST.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(processPhoneNumber(request.getPhoneNumber()));
        user.setAddress(request.getAddress());
        user.setRole(role);
        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse createSprayer(SprayerCreationRequest request) {
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_USED);
        }

        if (userRepository.existsByPhoneNumber(processPhoneNumber(request.getPhoneNumber()))) {
            throw new CustomException(ErrorCode.PHONE_NUMBER_USED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        Role role = roleRepository.findByName(RoleType.SPRAYER.name())
                .orElseThrow(() -> new RuntimeException("User's Role Not Found."));

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(processPhoneNumber(request.getPhoneNumber()));
        user.setAddress(request.getAddress());
        user.setRole(role);
        user.setExpertise(request.getExpertise());
        userRepository.save(user);

        return new UserResponse(user);
    }

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

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public UserResponse getUserByPhone(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        return new UserResponse(user);
    }

    public UserResponse getMyInfo() {
        Long userId = getCurrentUserId();
        System.out.println(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));
        return new UserResponse(user);
    }

    public UserResponse updateUser(Long userId, UserUpdateRequest request) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXISTS));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        boolean authorize = user.getEmail().equals(currentUsername) ||
                authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_RECEPTIONIST"));

        if (!authorize) {
            throw new AccessDeniedException("You do not have permission to update this user.");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(processPhoneNumber(request.getPhoneNumber()));
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return new UserResponse(user);
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.FARMER_NOT_EXIST));
        userRepository.deleteById(userId);
    }

    @Override
    public FarmerDTO findFarmerById(Long farmerId) {
        Optional<User> user = userRepository.findFarmerById(farmerId);
        return user.map(FarmerDTO::new)
                .orElse(null);
    }

    @Override
    public FarmerDTO findFarmerByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(FarmerDTO::new)
                .orElse(null);
    }

    @Override
    public FarmerDTO findFarmerByPhoneNumber(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(FarmerDTO::new)
                .orElse(null);
    }

    @Override
    public ReceptionistDTO findReceptionistById(Long receptionistId) {
        Optional<User> user = userRepository.findReceptionistById(receptionistId);
        return user.map(ReceptionistDTO::new)
                .orElse(null);
    }

    public UserAuthenticateDTO findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            System.out.println("Username from User entity: " + foundUser.getFullName());
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        return user.map(UserAuthenticateDTO::new)
                .orElse(null); // Return null if user is not found
    }


    @Override
    public UserAuthenticateDTO findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserAuthenticateDTO::new)
                .orElse(null); // Return null if user is not found
    }

    @Override
    public UserAuthenticateDTO findUserByPhoneNumber(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(UserAuthenticateDTO::new)
                .orElse(null); // Return null if user is not found
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public Page<SprayerDTO> getAvailableSprayers(List<Long> bookedSprayersId, Pageable pageable) {
        Page<User> page;

        if (bookedSprayersId == null || bookedSprayersId.isEmpty()) {
            // Fetch all sprayers
            page = userRepository.findAllByRoleName("SPRAYER", pageable);
        } else {
            // Fetch sprayers excluding those in the bookedSprayersId list
            page = userRepository.findPagedSprayersExcludeByIds(bookedSprayersId, pageable);
        }

        // Convert Page<User> to Page<SprayerDTO>
        List<SprayerDTO> sprayers = page.getContent()
                .stream()
                .map(SprayerDTO::new)
                .collect(Collectors.toList());

        // Return PageImpl with correct total number of elements
        return new PageImpl<>(sprayers, pageable, page.getTotalElements());
    }

    @Override
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public List<SprayerDTO> getAvailableSprayers(List<Long> bookedSprayersId) {
        // Fetch the list of SprayerDTO
        List<SprayerDTO> sprayers = userRepository.findAllSprayersExcludeByIds(bookedSprayersId)
                .stream()
                .map(user -> new SprayerDTO(user))
                .toList(); // Assuming the repository returns a stream

        return sprayers;
    }

    @Override
    public SprayerDTO findSprayerById(Long sprayerId) {
        Optional<User> user = userRepository.findSprayerById(sprayerId);
        return user.map(SprayerDTO::new)
                .orElse(null); // Return null if user is not found
    }

    private String processPhoneNumber(String phoneNumber) {
        // Remove all spaces from the phone number
        phoneNumber = phoneNumber.replaceAll("\\s+", "");

        // Replace the +84 with 0 if it starts with +84
        if (phoneNumber.startsWith("+84")) {
            phoneNumber = phoneNumber.replaceFirst("\\+84", "0");
        }

        return phoneNumber;
    }

    public void forgetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

        if (!user.getRole().toString().equals("FARMER")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String newPassword = randomizePassword();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        emailAPI.sendForgetPassword(email, newPassword);
    }

    private String randomizePassword() {

        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~:/?#[email protected]$&'()*+,;=";
        final int LENGTH = 12;
        final SecureRandom RANDOM = new SecureRandom();

        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                // Assuming CustomUserDetails holds the User ID
                return ((CustomUserDetails) principal).getId();
            }
        }

        throw new CustomException(ErrorCode.USER_NOT_EXISTS);
    }

    @PreAuthorize("hasAuthority('APPROVE_CREATE_FIELD')")
    public void addFieldToFarmer(Long farmerId, Long fieldId) {
        User farmer = userRepository.findFarmerById(farmerId)
                .orElseThrow(() -> new CustomException(ErrorCode.FARMER_NOT_EXIST));

        // Ensure fieldsId is initialized
        if (farmer.getFieldsId() == null) {
            farmer.setFieldsId(new ArrayList<>());
        }

        if (farmer.getFieldsId().size() == 10) {
            throw new CustomException(ErrorCode.FIELD_EXCEED);
        }

        farmer.getFieldsId().add(fieldId);
        userRepository.save(farmer);
    }
}