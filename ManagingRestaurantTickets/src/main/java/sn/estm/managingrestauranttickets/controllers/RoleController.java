/* RoleController class handling all services in RoleServiceImpl. */
package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//import org.springframework.security.access.prepost.PostAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.RoleService;

import java.util.List;


@Slf4j
@Data
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
/*@CrossOrigin(origins = {
        "http://localhost",
        "http://localhost:*",
        "http://10.0.2.2",
        "http://10.0.2.2:*",
        "http://127.0.0.1"
})*/
public class RoleController {
    
    private final RoleService roleService;

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        log.info("Creating role with details: {}", roleDTO);
       
        RoleDTO createdRole = roleService.createRole(roleDTO);
       
        log.info("Role created successfully with ID: {}", createdRole.getId());
       
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.readRoles();

        // Log pour debug
        System.out.println("Roles count: " + roles.size());
        roles.forEach(role ->
                System.out.println("Role: id=" + role.getId() + ", name=" + role.getName())
        );

        log.info("Fetched roles: {}", roles);

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/{roleId}", produces = "application/json")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long roleId) {
        RoleDTO role = roleService.readRoleByRoleId(roleId);
       
        log.info("Fetched role with ID: {}", roleId);

        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @GetMapping(value = "/name/{roleName}", produces = "application/json")
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String roleName) {
        RoleDTO role = roleService.readRoleByRoleName(roleName);
       
        log.info("Fetched role with name: {}", roleName);

        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{roleId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long roleId, @RequestBody RoleDTO roleDTO) {
        log.info("Updating role with ID: {} with details: {}", roleId, roleDTO);
       
        RoleDTO updatedRole = roleService.updateRole(roleDTO);
       
        log.info("Role updated successfully with ID: {}", updatedRole.getId());
        
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    //@PostAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        log.info("Deleting role with ID: {}", roleId);
        
        roleService.deleteRole(roleId);
        
        log.info("Role deleted successfully with ID: {}", roleId);
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
