/* UserController class handling all services in UserServiceImpl. */
package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//import org.springframework.security.access.prepost.PostAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import sn.estm.managingrestauranttickets.dto.LoginRequestDTO;
import sn.estm.managingrestauranttickets.dto.LoginResponseDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.exceptions.InvalidCredentialsException;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Data
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {

        log.info("Creating user with details: {}", userDTO);
       
        UserDTO createdUser = userService.createUser(userDTO);
       
        log.info("User created successfully with ID: {}", createdUser.getUserId());
       
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

   /* @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        UserDTO user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        log.info("User logged in successfully: {}", user.getUsername());

        return new ResponseEntity<>(user, HttpStatus.OK);
    }*/

/*    // ⭐ Connexion avec réponse détaillée
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        log.info("Login request for user: {}", loginRequest.getUsername());

        try {
            UserDTO user = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            log.info("Connexion réussie pour: {}", loginRequest.getUsername());

            return ResponseEntity.ok(LoginResponseDTO.success(user));
        } catch (ResourceNotFoundException e) {
            log.warn("Échec de connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(LoginResponseDTO.failure("Utilisateur non trouvé"));
        } catch (IllegalArgumentException e) {
            log.warn("Échec de connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponseDTO.failure("Mot de passe incorrect"));
        } catch (Exception e) {
            log.error("Erreur lors de la connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponseDTO.failure("Erreur interne du serveur"));
        }
    }

    // ⭐ Validation simple des identifiants
    @PostMapping(value = "/validate", consumes = "application/json",
                                      produces = "application/json")
    public ResponseEntity<Map<String, Object>> validateCredentials(
            @RequestBody LoginRequestDTO loginRequest) {
        log.info("Validation des identifiants pour: {}", loginRequest.getUsername());

        Map<String, Object> response = new HashMap<>();

        try {
            boolean isValid = userService.validateCredentials(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            response.put("success", isValid);
            response.put("message", isValid ? "Identifiants valides" : "Identifiants invalides");
            response.put("username", loginRequest.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la validation: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Erreur lors de la validation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }*/

    // ⭐ Vérification rapide si l'utilisateur existe
    @GetMapping(value = "/check/{username}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> checkUserExists(@PathVariable String username) {
        log.info("Vérification de l'existence de l'utilisateur: {}", username);

        Map<String, Object> response = new HashMap<>();

        try {
            boolean exists = userRepository.findByUsername(username).isPresent();

            response.put("exists", exists);
            response.put("username", username);
            response.put("message", exists ? "Utilisateur trouvé" : "Utilisateur non trouvé");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification: {}", e.getMessage());
            response.put("exists", false);
            response.put("message", "Erreur lors de la vérification");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UserDTO>> getAllUsers() {

        List<UserDTO> users = userService.readUsers();

        log.info("Fetched users: {}", users);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{userId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, 
                                              @RequestBody UserDTO userDTO) {

        log.info("Updating user with ID: {} with details: {}", userId, userDTO);

        UserDTO updatedUser = userService.updateUser(userDTO);

        log.info("User updated successfully with ID: {}", updatedUser.getUserId());
       
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {

        log.info("Deleting user with ID: {}", userId);
      
        userService.deleteUser(userId);
      
        log.info("User deleted successfully with ID: {}", userId);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @PutMapping(value = "/password/{userId}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@PathVariable Long userId, @RequestBody String password) {

        log.info("Updating password for user with ID: {}", userId);
       
        userService.updatePassword(userId, password);
       
        log.info("Password updated successfully for user with ID: {}", userId);
    }


      //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {

        log.info("Fetched user with ID: {}", userId);

        UserDTO user = userService.readUserByUserId(userId);
       

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/username/{username}", produces = "application/json")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {

        UserDTO user = userService.readUserByUsername(username);
       
        log.info("Fetched user with username: {}", username);
       
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public void addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        
        log.info("Adding role with ID: {} to user with ID: {}", roleId, userId);
       
        userService.addRoleToUser(userId, roleId);
       
        log.info("Role with ID: {} added to user with ID: {}", roleId, userId);
    }

}

