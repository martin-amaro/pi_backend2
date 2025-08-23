package com.example.pib2.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pib2.model.dto.BussinesPatchDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;
import com.example.pib2.repository.BusinessRepository;
import com.example.pib2.repository.UserRepository;
import com.example.pib2.service.BusinessService;

@Service
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    public Optional<Business> updateByUserEmail(String email, BussinesPatchDTO dto) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        Business business = userOpt.get().getBusiness();

        if (business == null) {
            return Optional.empty();
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            business.setName(dto.getName());
        }

        if (dto.getIndustry() != null && !dto.getIndustry().isBlank()) {
            business.setIndustry(dto.getIndustry());
        }

        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            business.setAddress(dto.getAddress());
        }

        return Optional.of(businessRepository.save(business));
    }

}
