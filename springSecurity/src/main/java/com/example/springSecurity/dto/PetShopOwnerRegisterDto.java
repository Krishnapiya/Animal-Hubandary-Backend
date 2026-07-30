package com.example.springSecurity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetShopOwnerRegisterDto {

    private String fname;
    private String lname;
    private String email;
    private String mobileNo;
    private String username;
    private String password;
    private String confirmPassword;
    private String ownerType;

}
