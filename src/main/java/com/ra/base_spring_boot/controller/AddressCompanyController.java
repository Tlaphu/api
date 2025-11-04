package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;
import com.ra.base_spring_boot.services.IAddressCompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company/addresses")
@RequiredArgsConstructor
public class AddressCompanyController {

    private final IAddressCompanyService addressCompanyService;

    /**
     *
     */
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<AddressCompanyResponse>>> getAll() {
        List<AddressCompanyResponse> addresses = addressCompanyService.getAllForCurrentCompany();

        return ResponseEntity.ok(
                ResponseWrapper.<List<AddressCompanyResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(addresses)
                        .build()
        );
    }

    /**
     *
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<AddressCompanyResponse>> create(
            @Valid @RequestBody FormAddressCompany form
    ) {
        AddressCompanyResponse response = addressCompanyService.create(form);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<AddressCompanyResponse>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data(response)
                        .build()
        );
    }

    /**
     *
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<AddressCompanyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FormAddressCompany form
    ) {
        AddressCompanyResponse response = addressCompanyService.update(id, form);

        return ResponseEntity.ok(
                ResponseWrapper.<AddressCompanyResponse>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(response)
                        .build()
        );
    }

    /**
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> delete(@PathVariable Long id) {
        addressCompanyService.delete(id);

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Address deleted successfully")
                        .build()
        );
    }
}
