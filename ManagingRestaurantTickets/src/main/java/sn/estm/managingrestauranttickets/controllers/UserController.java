package sn.estm.managingrestauranttickets.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PatchMapping;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    /*
        role  Admin:
        /api/users/
        Crud utilisateurs,
        Activer/Désactiver compte utilisateur
        addAccountToUser
        majProfil(=updateUser??) :  /{userId}/profil
     */
    final UserService userService;

    @PostMapping(consumes = "application/json")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @GetMapping(produces = "application/json")
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PutMapping(value = "/{userId}", consumes = "application/json")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDTO userDto) {
        return ResponseEntity.ok(userService.updateUser(userDto));
    }

    @DeleteMapping("/{userId}")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{userId}/password", consumes = "application/json")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId, @RequestBody String password) {
        userService.updatePassword(userId, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.readUserByUserId(userId));
    }

    @GetMapping(value = "/username/{username}", produces = "application/json")
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.readUserByUsername(username));
    }

    @PostMapping(value = "/{username}/roles/{roleName}", consumes = "application/json")
    //@PostAuthorize("hasAuthority('@PostAuthorize(\"hasAuthority('ADMIN')\")')")
    public ResponseEntity<Void> addRoleToUser(@PathVariable String username, @PathVariable String roleName) {
        userService.addRoleToUser(username, roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response){

    }

    //addCompteToUser:    @PostMapping(value = "/{username}/account/{accountName}"

/*    @GetMapping()
    public List<UserDTO> listUsers(){
        return userService.findAllUsers();
    }

    @PostMapping()
    public UserDTO saveUser(@RequestBody UserDTO userDTO){
        return  userService.createUser(userDTO);
    }

    @PostMapping(value = "/{username}/roles/{roleName}")
    public void  addRoleToUser(@RequestBody RoleUserForm roleUserForm){
        userService.addRoleToUser(roleUserForm.getUsername(), roleUserForm.getRoleName());
    }*/

}
@Data
class RoleUserForm{
    private String username;
    private String roleName;
}