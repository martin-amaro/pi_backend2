package com.example.pib2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.BusinessDTO;
import com.example.pib2.model.dto.BusinessResponseDTO;
import com.example.pib2.model.dto.BussinesPatchDTO;
import com.example.pib2.model.dto.CreateUserRequestDTO;
import com.example.pib2.model.dto.StaffUserResponseDTO;
import com.example.pib2.model.dto.UserDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.UserRepository;
import com.example.pib2.service.BusinessService;
import com.example.pib2.service.UserService;
import com.example.pib2.util.AuthUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/staff")
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

    @GetMapping("/staff/search")
    public ResponseEntity<?> searchUsersFromBusiness(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden acceder");
        }

        Business business = currentUser.getBusiness();

        if (business == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se encontró la empresa asociada al usuario");
        }

        // ajusta para que la página del front (1) coincida con la del backend (0)
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> userPage = userRepository.searchByBusinessAndNameOrEmail(business, query, pageable);

        List<UserDTO> dtos = userPage.getContent().stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", dtos);
        response.put("currentPage", userPage.getNumber() + 1); // le devolvemos 1-based al front
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalItems", userPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    

    @PostMapping("/staff")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequestDTO request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden acceder");
        }

        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está en uso");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setBusiness(currentUser.getBusiness());
        newUser.setRole(request.getRole());

        userRepository.save(newUser);

        return ResponseEntity.ok(
                new StaffUserResponseDTO(newUser.getId(), newUser.getName(), newUser.getEmail(), newUser.getRole()));

    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<?> deleteStaff(@Valid @PathVariable Long id) {

        try {
            Business business = AuthUtils.getCurrentBusiness();
            Optional<User> targetUser = userRepository.findById(id);

            if (targetUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            User user = targetUser.get();

            if (!business.getId().equals(user.getBusiness().getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no pertenece al negocio."));
            }

            boolean deleted = userService.delete(user.getId());

            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
