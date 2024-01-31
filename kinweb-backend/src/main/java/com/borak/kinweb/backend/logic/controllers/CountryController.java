/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.dto.country.CountryResponseDTO;
import com.borak.kinweb.backend.logic.services.country.ICountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mr. Poyo
 */
//@CrossOrigin(originPatterns = {"http://localhost:*"},maxAge = 3600,allowCredentials = "true")
@RestController
@RequestMapping(path = "api/countries")
public class CountryController {
    
    @Autowired
    private ICountryService countryService;
    
    //=========================GET MAPPINGS==================================  
    @GetMapping    
    public ResponseEntity getCountries() {
        return countryService.getAll();
    }
    
}
