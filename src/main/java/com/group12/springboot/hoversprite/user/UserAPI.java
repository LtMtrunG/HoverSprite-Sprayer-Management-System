package com.group12.springboot.hoversprite.user;

import java.nio.file.AccessDeniedException;

import com.group12.springboot.hoversprite.common.ListResponse;

public interface UserAPI {

    public UserResponse createFarmer(FarmerCreationRequest request);

    public UserResponse createReceptionist(ReceptionistCreationRequest request);

    public UserResponse createSprayer(SprayerCreationRequest request);

    public ListResponse<UserResponse> getUsersByRole(int pageNo, int pageSize, String role);

    public UserResponse getUserById(Long userId);

    public UserResponse getUserByPhone(String phoneNumber);

    public UserResponse getMyInfo();

    public UserResponse updateUser(Long userId, UserUpdateRequest request) throws AccessDeniedException;

    public void deleteUser(Long userId);

    public FarmerDTO findFarmerById(Long farmerId);

    public ReceptionistDTO findReceptionistById(Long receptionistId);

    public UserAuthenticateDTO findUserByEmail(String email);

    public UserAuthenticateDTO findUserByPhoneNumber(String phoneNumber);
}