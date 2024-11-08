package com.group12.springboot.hoversprite.field.service;

import com.group12.springboot.hoversprite.booking.BookingAPI;
import com.group12.springboot.hoversprite.booking.BookingDTO;
import com.group12.springboot.hoversprite.booking.enums.BookingStatus;
import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.field.*;
import com.group12.springboot.hoversprite.field.entity.Field;
import com.group12.springboot.hoversprite.field.repository.FieldRepository;
import com.group12.springboot.hoversprite.user.FarmerDTO;
import com.group12.springboot.hoversprite.user.ReceptionistDTO;
import com.group12.springboot.hoversprite.user.UserAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.nimbusds.oauth2.sdk.util.StringUtils.isNumeric;

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
        Long farmerId = request.getFarmerId();

        FarmerDTO farmerDTO;
        if (farmerId == null) {
            farmerId = userAPI.getCurrentUserId();
            farmerDTO = userAPI.findFarmerById(farmerId);
            if (farmerDTO == null) {
                throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
            }
        } else {
            farmerDTO = userAPI.findFarmerById(farmerId);
            if (farmerDTO == null) {
                throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
            }
            Long currentUserId = userAPI.getCurrentUserId();
            ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(currentUserId);
            if (receptionistDTO == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            if (!(request.getAddress().contains("Vietnam") || request.getAddress().contains("Viet Nam")
                    || request.getAddress().contains("Việt Nam"))) {
                throw new CustomException(ErrorCode.UNSUPPORTED_COUNTRIES);
            }
        }

        List<Long> fieldIds = farmerDTO.getFieldsId();

        if (fieldIds != null) {
            List<Field> fields = fieldRepository.findFieldsOfFarmer(fieldIds);

            // Check if any field has the same name as the requested field name
            for (Field existingField : fields) {
                if (existingField.getName().equalsIgnoreCase(request.getName())) {
                    throw new CustomException(ErrorCode.FIELD_NAME_ALREADY_EXISTS);
                } else if (existingField.getLatitude() == request.getLatitude() && existingField.getLongitude() == request.getLongitude()) {
                    throw new CustomException(ErrorCode.FIELD_LOCATION_EXISTS);
                }
            }
        }

        // Fetch the fields from the repository

        Field field = new Field();
        field.setName(request.getName());
        field.setAddress(request.getAddress());
        field.setLatitude(request.getLatitude());
        field.setLongitude(request.getLongitude());
        field.setCropType(request.getCropType());
        field.setFarmlandArea(request.getFarmlandArea());
        field.setTotalCost(request.getFarmlandArea() * 30000);

        Field savedField = fieldRepository.save(field);

        userAPI.addFieldToFarmer(farmerId, savedField.getId());

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
    public Page<FieldResponse> getFarmersFieldsPage(Long farmerId, int pageNo, int pageSize, String sortBy , String order, String keyword) {
        FarmerDTO farmerDTO;

        if (farmerId == null) {
            farmerId = userAPI.getCurrentUserId();
            farmerDTO = userAPI.findFarmerById(farmerId);
            if (farmerDTO == null) {
                throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
            }
        } else {
            farmerDTO = userAPI.findFarmerById(farmerId);
            if (farmerDTO == null) {
                throw new CustomException(ErrorCode.FARMER_NOT_EXIST);
            }
            Long currentUserId = userAPI.getCurrentUserId();
            ReceptionistDTO receptionistDTO = userAPI.findReceptionistById(currentUserId);
            if (receptionistDTO == null) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
        }

        Pageable pageable;
        if (sortBy.equals("id") || sortBy.equals("name") || sortBy.equals("lastSprayingDate")) {
            if (order.equals("ascending")) {
                pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
            }
            else if (order.equals("descending")){
                pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
            } else {
                pageable = PageRequest.of(pageNo, pageSize);
            }
        } else {
            pageable = PageRequest.of(pageNo, pageSize);
        }

        Page<Field> fieldsPage;

        // Normalize status to upper case
        String normalizedKeyword = keyword.toUpperCase();

        if (keyword.isEmpty()) {
            fieldsPage = fieldRepository.findByIdIn(farmerDTO.getFieldsId(), pageable);
        } else {
            fieldsPage = fieldRepository.findByIdInAndNameOrCropTypeContainingKeyword(farmerDTO.getFieldsId(), keyword, pageable);
        }

        // Map Page<Field> to Page<FieldResponse> using existing constructor
        List<FieldResponse> fieldResponses = fieldsPage.stream()
                .map(FieldResponse::new) // Use the constructor directly
                .collect(Collectors.toList());

        return new PageImpl<>(fieldResponses, pageable, fieldsPage.getTotalElements());
    }

    private ListResponse<FieldResponse> listToListResponse(List<FieldResponse> list, Page<Field> fieldPage) {
        ListResponse<FieldResponse> listResponse = new ListResponse<>();
        listResponse.setContent(list);
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

        List<Long> fieldIds = farmerDTO.getFieldsId();

        if (fieldIds != null || !fieldIds.isEmpty()) {
            List<Field> fields = fieldRepository.findFieldsOfFarmer(fieldIds);

            // Check if any field has the same name as the requested field name
            for (Field existingField : fields) {
                if (!Objects.equals(existingField.getId(), request.getFieldId()) && existingField.getName().equalsIgnoreCase(request.getName())) {
                    throw new CustomException(ErrorCode.FIELD_NAME_ALREADY_EXISTS);
                } else if (!Objects.equals(existingField.getId(), request.getFieldId()) && existingField.getLatitude() == request.getLatitude() && existingField.getLongitude() == request.getLongitude()) {
                    throw new CustomException(ErrorCode.FIELD_LOCATION_EXISTS);
                }
            }
        }

        field.setName(request.getName());
        field.setAddress(request.getAddress());
        field.setLatitude(request.getLatitude());
        field.setLongitude(request.getLongitude());
        field.setFarmlandArea(request.getFarmlandArea());
        field.setCropType(request.getCropType());
        field.setTotalCost(request.getFarmlandArea() * 30000);

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

    @Override
    public void updateLastSprayingDate(Long id) {
        Field field = fieldRepository.findFieldById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.FIELD_NOT_EXIST));
        LocalDate currentDate = LocalDate.now();
        field.setLastSprayingDate(currentDate);
    }

    @Override
    public List<Object[]> findFieldCoordinatesByIds(List<Long> fieldIds) {
        return fieldRepository.findFieldCoordinatesByIds(fieldIds);
    }
}
