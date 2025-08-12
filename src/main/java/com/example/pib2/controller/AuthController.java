package com.example.pib2.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.BusinessDTO;
import com.example.pib2.model.dto.LoginDTO;
import com.example.pib2.model.dto.UserLoginResponseDTO;
import com.example.pib2.model.dto.UserRegisterDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.BusinessRepository;
import com.example.pib2.security.TokenService;
import com.example.pib2.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

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

    public AuthController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();

            var credentials = new UsernamePasswordAuthenticationToken(email, password);
            @SuppressWarnings("unused")
            var auth = this.authenticationManager.authenticate(credentials);
            String token = tokenService.generateToken(email);

            return ResponseEntity.ok(new UserLoginResponseDTO(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error inesperado"));
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String name = request.getName();
        // UserRole role = request.getRole();

        if (userService.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado");
        }

        // Crear el negocio vacío
        Business emptyBusiness = new Business();
        emptyBusiness.setName(""); // o null si prefieres
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

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !((Authentication) authentication).isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        String email = ((Authentication) authentication).getName();

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Business business = user.getBusiness();

            BusinessDTO businessDTO = null;
            if (business != null) {
                businessDTO = new BusinessDTO(
                    business.getId(),
                    business.getName(),
                    business.getTicker(),
                    business.getSector(),
                    business.getIndustry(),
                    business.getDescription(),
                    business.getAddress());
            }

            UserLoginResponseDTO dto = new UserLoginResponseDTO(user.getEmail());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

}
