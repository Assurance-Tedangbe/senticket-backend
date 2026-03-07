package sn.estm.managingrestauranttickets.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.estm.managingrestauranttickets.dto.LoginRequestDTO;
import sn.estm.managingrestauranttickets.dto.LoginResponseDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenController {

    private final UserService userService;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("🔐 Tentative de connexion pour: {}", loginRequest.getUsername());

        try {
            UserDTO user = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            log.info("✅ Connexion réussie pour: {}", loginRequest.getUsername());

            return ResponseEntity.ok(LoginResponseDTO.success(user));
        } catch (Exception e) {
            log.warn("❌ Échec de connexion: {}", e.getMessage());

            String errorMessage = "Échec de l'authentification";
            if (e.getMessage().contains("non trouvé")) {
                errorMessage = "Utilisateur non trouvé";
            } else if (e.getMessage().contains("Mot de passe")) {
                errorMessage = "Mot de passe incorrect";
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDTO.failure(errorMessage));
        }
    }

    @PostMapping(value = "/validate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> validate(@RequestBody LoginRequestDTO loginRequest) {
        log.info("🔍 Validation des identifiants pour: {}", loginRequest.getUsername());

        Map<String, Object> response = new HashMap<>();

        try {
            UserDTO user = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            response.put("success", true);
            response.put("message", "Authentification réussie");
            response.put("user", user);
            response.put("userId", user.getId());
            response.put("role", user.getRoleDTO());

            log.info("✅ Validation réussie pour: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("❌ Validation échouée: {}", e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}