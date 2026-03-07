package sn.estm.managingrestauranttickets.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest { // DTO for authentication request

    private String username;
    private String password;
}
