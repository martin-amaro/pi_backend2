package com.example.pib2.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.BusinessDTO;
import com.example.pib2.model.dto.BusinessResponseDTO;
import com.example.pib2.model.dto.BussinesPatchDTO;
import com.example.pib2.model.dto.UserDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.UserRepository;
import com.example.pib2.service.BusinessService;
import com.example.pib2.util.AuthUtils;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessService businessService;

    @PostMapping("/me")
    public ResponseEntity<?> getBusiness() {
        try {
            Business business = AuthUtils.getCurrentBusiness();
            return ResponseEntity.ok(BusinessDTO.fromEntity(business));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }

    }

    @PatchMapping("/me")
    public ResponseEntity<?> patchAuthenticatedBusiness(@RequestBody BussinesPatchDTO dto) {

        try {
            String email = AuthUtils.getCurrentEmail();
            Optional<Business> updatedBusinessOpt = businessService.updateByUserEmail(email, dto);

            if (updatedBusinessOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Business updatedBusiness = updatedBusinessOpt.get();
            BusinessResponseDTO responseDTO = BusinessResponseDTO.fromEntity(updatedBusiness);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }

    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsersFromBusiness(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden acceder");
        }

        Business business = currentUser.getBusiness();

        if (business == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se encontró la empresa asociada al usuario");
        }

        List<User> users = userRepository.findByBusiness(business);
        List<UserDTO> dtos = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
