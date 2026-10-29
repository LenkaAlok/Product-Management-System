package com.jforce.productmanagement.service;
import com.jforce.productmanagement.entity.Address;
import com.jforce.productmanagement.entity.User;
import com.jforce.productmanagement.repository.AddressRepository;
import com.jforce.productmanagement.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String)) {
            throw new RuntimeException("User is not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public Address addAddress(Address address) {
        User user = getLoggedInUser();

        address.setId(null);
        address.setUser(user);

        return addressRepository.save(address);
    }

    @Transactional(readOnly = true)
    public List<Address> getMyAddresses() {
        User user = getLoggedInUser();

        return addressRepository.findByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public Address getAddressById(Long addressId) {
        User user = getLoggedInUser();

        return addressRepository
                .findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));
    }

    @Transactional
    public Address updateAddress(Long addressId, Address updatedAddress) {
        User user = getLoggedInUser();

        Address address = addressRepository
                .findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));

        address.setStreet(updatedAddress.getStreet());
        address.setCity(updatedAddress.getCity());
        address.setState(updatedAddress.getState());
        address.setPostalCode(updatedAddress.getPostalCode());
        address.setCountry(updatedAddress.getCountry());

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        User user = getLoggedInUser();

        Address address = addressRepository
                .findByIdAndUserId(addressId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));

        addressRepository.delete(address);
    }
}
