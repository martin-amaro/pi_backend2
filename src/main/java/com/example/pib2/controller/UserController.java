package com.example.pib2.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.UserLoginDTO;
import com.example.pib2.model.dto.UserLoginResponseDTO;
import com.example.pib2.model.dto.UserRegisterDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.BusinessRepository;
import com.example.pib2.security.TokenService;
import com.example.pib2.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "basicAuth")

public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();

        if (userService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "El correo ya está registrado"));
        }

        // Crear el negocio vacío
        Business emptyBusiness = new Business();
        emptyBusiness.setName("");
        Business savedBusiness = businessRepository.save(emptyBusiness);

        // Crear el usuario y asociar el negocio
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setName(name);
        newUser.setBusiness(savedBusiness);
        newUser.setRole(UserRole.ADMIN);

        User savedUser = userService.save(newUser);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            User user = userService.findByEmail(email).orElse(null);

            String name = user.getName();

            var credentials = new UsernamePasswordAuthenticationToken(email, password);
            @SuppressWarnings("unused")
            var auth = this.authenticationManager.authenticate(credentials);
            String token = tokenService.generateToken(email);

            return ResponseEntity.ok(new UserLoginResponseDTO(
                name,
                email,
                user.getRole(),
                user.getProvider(),
                token
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas"));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado"));
        }

    }

    @PostMapping("/oauth")
    public ResponseEntity<?> loginFromProvider(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String name = (String) request.get("name");
        String provider = (String) request.get("provider");
        String providerId = (String) request.get("providerId");

        try {
            // Ya existe un usuario con ese providerId
            User user = userService.findByProviderId(providerId).orElse(null);

            if (user == null) {
                // Existe por email
                user = userService.findByEmail(email).orElse(null);

                if (user == null) {
                    // Si no existe, crearlo
                    Business emptyBusiness = new Business();
                    emptyBusiness.setName("");
                    Business savedBusiness = businessRepository.save(emptyBusiness);

                    user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setPassword("null");
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    user.setBusiness(savedBusiness);
                    user.setRole(UserRole.USER);

                    user = userService.save(user);
                } else {
                    // Existe por email → actualizarle provider info
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    userService.save(user);
                }
            }

            // 5. Generar token
            String token = tokenService.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("provider", user.getProvider());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error en OAuth login"));
        }
    }

}
