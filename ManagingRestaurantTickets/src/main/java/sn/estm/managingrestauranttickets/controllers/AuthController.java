package sn.estm.managingrestauranttickets.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.estm.managingrestauranttickets.entities.AuthRequest;
import sn.estm.managingrestauranttickets.entities.AuthResponse;
import sn.estm.managingrestauranttickets.services.CustomUserDetailsService;
import sn.estm.managingrestauranttickets.services.JwtService;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthController {
    /**
     * This controller will provide an endpoint to authenticate and generate the JWT token
     */

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * this method aims to authenticate the user and return a JWT token
     * it authenticates a user using their username and password,
     * and if the credentials are valid, generate and return a JWT token
     * so the user can securely access protected endpoints in the application
     *
     * handles a login request by verifying the user's credentials using Spring Security,
     * and if authentication is successful, it generates a JWT token and returns it
     * in a structured JSON response. This allows the client to use the token
     * for accessing protected resources.
     * @param authRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword())
        );

        final UserDetails user = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(token));
    }

}
