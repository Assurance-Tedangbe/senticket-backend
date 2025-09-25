/* UserController class handling all services in UserServiceImpl. */
package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        log.info("Creating user with details: {}", userDTO);
       
        UserDTO createdUser = userService.createUser(userDTO);
       
        log.info("User created successfully with ID: {}", createdUser.getUserId());
       
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
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
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
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
    @PutMapping(value = "/{userId}/password", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable Long userId, @RequestBody String password) {
        log.info("Updating password for user with ID: {}", userId);
       
        userService.updatePassword(userId, password);
       
        log.info("Password updated successfully for user with ID: {}", userId);
    }


      //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO user = userService.readUserByUserId(userId);
       
        log.info("Fetched user with ID: {}", userId);
       
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/username/{username}", produces = "application/json")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.readUserByUsername(username);
       
        log.info("Fetched user with username: {}", username);
       
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}

