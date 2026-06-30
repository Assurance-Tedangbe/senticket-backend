package sn.estm.managingrestauranttickets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private UserDTO user;
    private String token;

    // Méthodes statiques pour créer des réponses standard
    public static LoginResponseDTO success(UserDTO user, String token) {
        return LoginResponseDTO.builder()
                .success(true)
                .message("Connexion réussie")
                .user(user)
                .token(token)
                .build();
    }

    public static LoginResponseDTO failure(String message) {
        return LoginResponseDTO.builder()
                .success(false)
                .message(message)
                .user(null)
                .token(null)
                .build();
    }
}