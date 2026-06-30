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
import sn.estm.managingrestauranttickets.security.jwt.JwtUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenController {

    private final UserService userService;
    private final JwtUtils jwtUtils;      //ICI

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("🔐 Tentative de connexion pour: {}", loginRequest.getUsername());

        try {
            UserDTO user = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // Génération du token JWT         //ICI
            String token = jwtUtils.generateJwtToken(
                    user.getUsername(),
                    user.getId(),
                    user.getRoleDTO().getName()
            );

            log.info("✅ Connexion réussie pour: {}", loginRequest.getUsername());

            return ResponseEntity.ok(LoginResponseDTO.success(user, token));  //ICI
        } catch (Exception e) {
            log.warn("Échec de connexion: {}", e.getMessage());

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

    // endpoint lié au logout service non commenté
   /* @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        // Extraire le username du token (si nécessaire)
        String token = authHeader.replace("Bearer ", "");
        String username = extractUsernameFromToken(token); // méthode utilitaire

        userService.logout(username);
        return ResponseEntity.ok("Déconnexion réussie");
    }

    private String extractUsernameFromToken(String token) {
        // Utilisez votre JwtUtils pour extraire le username
        return JwtUtils.extractUsername(token);
    }*/

   /* @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam(required = false) String username) {
        // Si vous utilisez Spring Security, vous pouvez récupérer le username depuis le contexte de sécurité
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // String username = auth.getName();

        // Pour l'exemple, on passe le username en paramètre ou on le récupère d'une autre manière
        String responseMessage = userService.logout(username != null ? username : "inconnu");
        log.info("Logout effectué: {}", responseMessage);
        return ResponseEntity.ok(responseMessage);
    }*/
}