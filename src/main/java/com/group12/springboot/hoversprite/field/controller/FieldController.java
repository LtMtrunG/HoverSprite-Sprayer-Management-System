package com.group12.springboot.hoversprite.field.controller;

import com.group12.springboot.hoversprite.common.ApiResponse;
import com.group12.springboot.hoversprite.common.ListResponse;
import com.group12.springboot.hoversprite.field.FieldCreationRequest;
import com.group12.springboot.hoversprite.field.FieldResponse;
import com.group12.springboot.hoversprite.field.FieldUpdateRequest;
import com.group12.springboot.hoversprite.field.service.FieldService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fields")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    @PostMapping("/create")
    ApiResponse<FieldResponse> createField(@RequestBody @Valid FieldCreationRequest request) {
        ApiResponse<FieldResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(fieldService.createField(request));
        return apiResponse;
    }

    @GetMapping("/all/list")
    ApiResponse<List<FieldResponse>> getFarmersFieldsList(@RequestParam("id") Long farmerId) {
        ApiResponse<List<FieldResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(fieldService.getFarmersFieldsList(farmerId));
        return apiResponse;
    }

    @GetMapping("/all/page")
    ApiResponse<Page<FieldResponse>> getFarmersFieldsPage(
            @RequestParam(value = "id", required = false) Long farmerId,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("order") String order,
            @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword){
        ApiResponse<Page<FieldResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(fieldService.getFarmersFieldsPage(farmerId, pageNo, pageSize, sortBy, order, keyword));
        return apiResponse;
    }

    @GetMapping("/get")
    ApiResponse<FieldResponse> getField(@RequestParam("id") Long fieldId) {
        ApiResponse<FieldResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(fieldService.getFieldById(fieldId));
        return apiResponse;
    }

    @PutMapping("/update")
    ApiResponse<FieldResponse> updateField(@RequestBody FieldUpdateRequest request) {
        ApiResponse<FieldResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(fieldService.updateField(request));
        return apiResponse;
    }

    @DeleteMapping("/delete")
    ApiResponse<String> deleteField(@RequestParam("id") Long fieldId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setResult(fieldService.deleteField(fieldId));
        return apiResponse;
    }
}
