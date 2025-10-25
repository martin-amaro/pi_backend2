package com.example.pib2.util;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;

@Component
public class AuthUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No autenticado");
        }
        return (User) authentication.getPrincipal();
    }

    public static String getCurrentEmail() {
        return getCurrentUser().getUsername(); // o getEmail()
    }

    public static Business getCurrentBusiness() {
        User user = getCurrentUser();
        Business business = user.getBusiness();
        if (business == null) {
            throw new RuntimeException("No se encontró empresa asociada");
        }
        return business;
    }

    public static void checkPermissions(User user, List<UserRole> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new RuntimeException("No se especificaron roles para la verificación");
        }
        UserRole userRole = user.getRole();
        if (userRole == null || !roles.contains(userRole)) {
            throw new RuntimeException("Permisos insuficientes");
        }
    }
}
