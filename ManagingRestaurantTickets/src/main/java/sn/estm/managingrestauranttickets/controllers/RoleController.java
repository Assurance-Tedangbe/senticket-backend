package sn.estm.managingrestauranttickets.controllers;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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

import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.RoleService;

import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDto) {
        return ResponseEntity.ok(roleService.createRole(roleDto));
    }

    @GetMapping(produces = "application/json")
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @GetMapping(value = "/{roleId}", produces = "application/json")
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.readRoleByRoleId(roleId));
    }

    @GetMapping(value = "/name/{roleName}", produces = "application/json")
    //@PostAuthorize("hasAnyAuthority('ADMIN', 'AGENT', 'ETUDIANT', 'PORTIER')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String roleName) {
        return ResponseEntity.ok(roleService.readRoleByRoleName(roleName));
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleDTO> updateRole(@RequestBody RoleDTO roleDto) {
        return ResponseEntity.ok(roleService.updateRole(roleDto));
    }

    @DeleteMapping("/{roleId}")
    //@PostAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
