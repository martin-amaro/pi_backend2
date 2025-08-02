package com.example.pib2.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BusinessDTO { 
    private long id; 
    private String name; 
    private String ticker; 
    private double sector;  
    private double industry; 
    private double description; 
    private double address; 
    
}
