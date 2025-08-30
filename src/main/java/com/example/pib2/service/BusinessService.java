package com.example.pib2.service;

import java.util.Optional;

import com.example.pib2.model.dto.BussinesPatchDTO;
import com.example.pib2.model.entity.Business;

public interface BusinessService {
    
    Optional<Business> updateByUserEmail(String email, BussinesPatchDTO dto);
    
}
