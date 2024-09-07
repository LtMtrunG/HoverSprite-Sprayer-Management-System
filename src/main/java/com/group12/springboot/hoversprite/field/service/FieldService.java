package com.group12.springboot.hoversprite.field.service;

import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.booking.BookingDTO;
import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.field.*;
import com.group12.springboot.hoversprite.field.entity.Field;
import com.group12.springboot.hoversprite.field.repository.FieldRepository;
import com.group12.springboot.hoversprite.user.FarmerDTO;
import com.group12.springboot.hoversprite.user.UserAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class FieldService implements FieldAPI {

    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private UserAPI userAPI;

    private final BookingAPI bookingAPI;

    public FieldService(@Lazy BookingAPI bookingAPI) {
        this.bookingAPI = bookingAPI;
    }

    @PreAuthorize("hasAuthority('APPROVE_CREATE_FIELD')")
    public FieldResponse createField(FieldCreationRequest request) {
        FarmerDTO farmerDTO = userAPI.findFarmerById(request.getFarmerId());

        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }

        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            if (!(request.getAddress().contains("Vietnam") || request.getAddress().contains("Viet Nam")
                    || request.getAddress().contains("Việt Nam"))) {
                throw new CustomException(ErrorCode.UNSUPPORTED_COUNTRIES);
            }
        }

        Field field = new Field();
        field.setName(request.getName());
        field.setAddress(request.getAddress());
        field.setLatitude(request.getLatitude());
        field.setLongitude(request.getLongitude());
        field.setCropType(request.getCropType());
        field.setFarmlandArea(request.getFarmlandArea());

        Field savedField = fieldRepository.save(field);

        userAPI.addFieldToFarmer(request.getFarmerId(), savedField.getId());

        return new FieldResponse(field);
    }

    @Override
    public FieldDTO findFieldById(Long id) {
        Optional<Field> field = fieldRepository.findFieldById(id);
        return field.map(FieldDTO::new)
                .orElse(null); // Return null if user is not found
    }

    @PreAuthorize("hasAuthority('APPROVE_VIEW_FIELD')")
    public List<FieldResponse> getFarmersFieldsList(Long farmerId) {
        FarmerDTO farmerDTO = userAPI.findFarmerById(farmerId);

        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }

        List<Long> fieldIds = farmerDTO.getFieldsId();

        if (fieldIds == null || fieldIds.isEmpty()) {
            return Collections.emptyList();  // return empty list if no fields are associated
        }

        // Fetch the fields from the repository
        List<Field> fields = fieldRepository.findFieldsOfFarmer(fieldIds);

        // Convert List<Field> to List<FieldResponse>
        return fields.stream()
                .map(FieldResponse::new)
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAuthority('APPROVE_VIEW_FIELD')")
    public ListResponse<FieldResponse> getFarmersFieldsPage(Long farmerId, int pageNo, int pageSize) {
        FarmerDTO farmerDTO = userAPI.findFarmerById(farmerId);
        if (farmerDTO == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXISTS);
        }

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Field> fieldPage = fieldRepository.findFieldsOfFarmer(farmerDTO.getFieldsId(), pageable);

        List<FieldResponse> fieldResponses = fieldPage.getContent().stream()
                .map(FieldResponse::new)
                .toList();

        ListResponse<FieldResponse> listResponse = new ListResponse<>();
        listResponse.setContent(fieldResponses);
        listResponse.setPageNo(fieldPage.getNumber());
        listResponse.setPageSize(fieldPage.getSize());
        listResponse.setTotalPages(fieldPage.getTotalPages());
        listResponse.setTotalSize(fieldPage.getTotalElements());
        listResponse.setLast(fieldPage.isLast());

        return listResponse;
    }

    @PreAuthorize("hasAuthority('APPROVE_VIEW_FIELD')")
    public FieldResponse getFieldById(Long fieldId) {
        Field field = fieldRepository.findFieldById(fieldId)
                .orElseThrow(() -> new CustomException(ErrorCode.FIELD_NOT_EXIST));
        // Return FieldResponse with the found Field
        return new FieldResponse(field);
    }

    @PreAuthorize("hasRole('FARMER')")
    public FieldResponse updateField(FieldUpdateRequest request) {
        Field field = fieldRepository.findFieldById(request.getFieldId())
                .orElseThrow(() -> new CustomException(ErrorCode.FIELD_NOT_EXIST));

        Long currentUserId = userAPI.getCurrentUserId();
        FarmerDTO farmerDTO = userAPI.findFarmerById(currentUserId);

        if (!farmerDTO.getFieldsId().contains(request.getFieldId())) {
            throw new CustomException(ErrorCode.FARMER_NOT_OWN_FIELD);
        }

        List<BookingDTO> bookingDTOList = bookingAPI.findIncompleteBookingByFieldId(request.getFieldId());
        if (!bookingDTOList.isEmpty()) {
            throw new CustomException(ErrorCode.FIELD_IN_BOOKING);
        }

        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            if (!(request.getAddress().contains("Vietnam") || request.getAddress().contains("Viet Nam")
                    || request.getAddress().contains("Việt Nam"))) {
                throw new CustomException(ErrorCode.UNSUPPORTED_COUNTRIES);
            }
        }

        field.setName(request.getName());
        field.setAddress(request.getAddress());
        field.setLatitude(request.getLatitude());
        field.setLongitude(request.getLongitude());
        field.setFarmlandArea(request.getFarmlandArea());
        field.setCropType(request.getCropType());

        fieldRepository.save(field);

        return new FieldResponse(field);
    }

    @PreAuthorize("hasRole('FARMER')")
    public String deleteField(Long fieldId) {
        Field field = fieldRepository.findFieldById(fieldId)
                .orElseThrow(() -> new CustomException(ErrorCode.FIELD_NOT_EXIST));

        Long currentUserId = userAPI.getCurrentUserId();
        FarmerDTO farmerDTO = userAPI.findFarmerById(currentUserId);

        if (!farmerDTO.getFieldsId().contains(fieldId)) {
            throw new CustomException(ErrorCode.FARMER_NOT_OWN_FIELD);
        }

        List<BookingDTO> bookingDTOList = bookingAPI.findIncompleteBookingByFieldId(fieldId);
        if (!bookingDTOList.isEmpty()) {
            throw new CustomException(ErrorCode.FIELD_IN_BOOKING);
        }

        fieldRepository.delete(field);

        return "Field deleted successfully";
    }
}
