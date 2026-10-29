package com.jforce.productmanagement.controller;
import com.jforce.productmanagement.entity.Address;
import com.jforce.productmanagement.service.AddressService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(
            @Valid @RequestBody Address address) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(address));
    }

    @GetMapping
    public ResponseEntity<List<Address>> getMyAddresses() {
        return ResponseEntity.ok(addressService.getMyAddresses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(
            @PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody Address address) {
        return ResponseEntity.ok(
                addressService.updateAddress(id, address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}