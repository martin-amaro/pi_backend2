package com.example.pib2.model.dto;

import com.example.pib2.model.entity.Business;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BusinessDTO { 
    private long id; 
    private String name; 
    private String ticker; 
    private String sector;  
    private String industry; 
    private String description; 
    private String address;
    
    public static Object fromEntity(Business business) {
        return new BusinessDTO(
            business.getId(),
            business.getName(),
            business.getTicker(),
            business.getSector(),
            business.getIndustry(),
            business.getDescription(),
            business.getAddress()
        );
    } 
    
}
