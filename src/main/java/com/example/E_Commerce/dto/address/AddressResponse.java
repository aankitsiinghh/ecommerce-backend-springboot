package com.example.E_Commerce.dto.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String street;
    private String phone;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private Boolean isDefault;
    private Long buyerId;
}
