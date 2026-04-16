/*
package sn.estm.managingrestauranttickets.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {
    */
/**
     * This controller will provide an endpoint to authenticate and generate the JWT token
     *//*


    final AuthenticationManager authenticationManager;
    final JwtService jwtService;
    final CustomUserDetailsService userDetailsService;

    */
/**
     * this method aims to authenticate the user and return a JWT token
     * It handles a login request by verifying the user's credentials using Spring Security,
     * and if the credentials are valid, generate  a JWT token and returns it in a structured
     * JSON response so the user can use the token for accessing protected resources
     * @param authRequest
     * @return
     *//*

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest authRequest) {
        */
/* Takes a JSON payload with username and password from the client *//*


        */
/* Verifies the credentials using Spring Security.
           If invalid, it throws an exception automatically *//*

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                        authRequest.getPassword())
        );

        */
/* Loads full user details (roles, permissions, etc.) by username  *//*

        final UserDetails user = userDetailsService.loadUserByUsername(authRequest.getUsername());

        */
/* Generates a JWT using the user’s username  *//*

        final String token = jwtService.generateToken(user);

        */
/* Returns a 200 OK response with a JSON body *//*

        return ResponseEntity.ok(new AuthResponse(token));
    }

}
*/
