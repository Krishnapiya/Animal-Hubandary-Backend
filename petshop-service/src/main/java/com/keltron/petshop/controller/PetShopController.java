package com.keltron.petshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/petshop")
public class PetShopController {

    @GetMapping("/test")
    public String test() {
        return "Pet Shop Service Working";
    }
}