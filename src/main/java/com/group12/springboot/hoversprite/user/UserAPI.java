package com.group12.springboot.hoversprite.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAPI {

    public FarmerDTO findFarmerById(Long farmerId);

    public FarmerDTO findFarmerByEmail(String email);

    public FarmerDTO findFarmerByPhoneNumber(String phoneNumber);

    public ReceptionistDTO findReceptionistById(Long receptionistId);

    public SprayerDTO findSprayerById(Long sprayerId);

    public UserAuthenticateDTO findUserById(Long id);

    public UserAuthenticateDTO findUserByEmail(String email);

    public UserAuthenticateDTO findUserByPhoneNumber(String phoneNumber);

    public Page<SprayerDTO> getAvailableSprayers(List<Long> bookedSprayersId, Pageable pageable);

    public List<SprayerDTO> getAvailableSprayers(List<Long> bookedSprayersId);
}