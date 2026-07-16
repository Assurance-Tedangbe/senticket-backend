/* UserController class handling all services in UserServiceImpl. */
package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;
import org.springframework.security.core.Authentication;

import java.util.List;

@Slf4j
//@Data
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    /// ============ CRÉATION (public — inscription libre) ============
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {

        log.info("Creating user with details: {}", userDTO);
       
        UserDTO createdUser = userService.createUser(userDTO);
       
        log.info("User created successfully with ID: {}", createdUser.getId());
       
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /// LISTE COMPLÈTE (ADMIN seulement — géré par SecurityConfig)
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<UserDTO> users = userService.readUsers();

        log.info("Fetched users: {}", users);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /// ============ MODIFIER SON PROFIL ============
    @PutMapping(value = "/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, 
                                              @RequestBody UserDTO userDTO,
                                              Authentication authentication) {

        log.info("Updating user with ID: {} with details: {}", userId, userDTO);
        UserDTO existingUser = userService.readUserByUserId(userId);

        // Un utilisateur ne peut modifier que son propre profil, sauf l'ADMIN
        if (!isOwnerOrAdmin(existingUser.getUsername(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDTO updatedUser = userService.updateUser(userDTO);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /// ========== SUPPRIMER (ADMIN seulement — géré par SecurityConfig) ==========
    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {

        log.info("Deleting user with ID: {}", userId);
      
        userService.deleteUser(userId);
      
        log.info("User deleted successfully with ID: {}", userId);
    }

    /// ============ CHANGER SON MOT DE PASSE ============
    @PutMapping(value = "/password/{userId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId,
                               @RequestBody String password,
                               Authentication authentication) {

        log.info("Updating password for user with ID: {}", userId);

        UserDTO existingUser = userService.readUserByUserId(userId);

        // Un utilisateur ne peut changer que son propre mot de passe, sauf l'ADMIN
        if (!isOwnerOrAdmin(existingUser.getUsername(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.updatePassword(userId, password);
       
        log.info("Password updated successfully for user with ID: {}", userId);

        return ResponseEntity.ok().build();
    }

    /// VOIR UN PROFIL PAR ID
    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId,
                                               Authentication authentication) {

        log.info("Fetched user with ID: {}", userId);

        UserDTO user = userService.readUserByUserId(userId);

        // Un utilisateur ne peut voir que son propre profil, sauf l'ADMIN
        if (!isOwnerOrAdmin(user.getUsername(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /// VOIR UN PROFIL PAR USERNAME
    @GetMapping(value = "/username/{username}", produces = "application/json")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username,
                                                     Authentication authentication) {

        log.info("Fetched user with username: {}", username);

        UserDTO user = userService.readUserByUsername(username);

        // Un utilisateur ne peut voir que son propre profil, sauf l'ADMIN
        if (!isOwnerOrAdmin(user.getUsername(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
       
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /** MÉTHODE UTILITAIRE
     * Vérifie que l'utilisateur connecté est le propriétaire du compte
     * ou qu'il a le rôle ADMIN.
     * Appelée sur toutes les actions sensibles (voir, modifier, changer mdp).
     */
    private boolean isOwnerOrAdmin(String targetUsername, Authentication authentication) {
        // authentication.getName() retourne le username extrait du token JWT, sans aucun accès BDD
        String connectedUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return connectedUsername.equals(targetUsername) || isAdmin;
    }
}

