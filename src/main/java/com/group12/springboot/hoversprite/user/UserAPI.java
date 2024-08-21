package com.group12.springboot.hoversprite.user;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group12.springboot.hoversprite.common.ListResponse;
public interface UserAPI {

    public FarmerExternalSignUpInfoResponse receiveFarmerGmailInfo(FarmerExternalSignUpInfoRequest request);

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

    public FarmerDTO findFarmerByEmail(String email);

    public FarmerDTO findFarmerByPhoneNumber(String phoneNumber);

    public ReceptionistDTO findReceptionistById(Long receptionistId);

    public SprayerDTO findSprayerById(Long sprayerId);

    public UserAuthenticateDTO findUserByEmail(String email);

    public UserAuthenticateDTO findUserByPhoneNumber(String phoneNumber);

    public Page<SprayerDTO> getAvailableSprayers(List<Long> bookedSprayersId, Pageable pageable);

    public List<SprayerDTO> getAvailableSprayers(List<Long> bookedSprayersId);
}