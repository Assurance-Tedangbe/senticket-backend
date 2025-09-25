/* RoleServiceImpl class implementing the RoleService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.exceptions.ForbiddenActionException;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.RoleMapper;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.RoleService;
import java.util.List;
import java.util.stream.Collectors;
import java.text.MessageFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /* RoleServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    } */

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        /* Checking if resource already exists */
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new ForbiddenActionException(HttpStatus.FORBIDDEN, "Role with name " + roleDTO.getName() + " already exists");
        }

        Role role = roleMapper.toEntity(roleDTO);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    public List<RoleDTO> readRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDTO readRoleByRoleId(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Role not found with ID: {0}", roleId)));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDTO readRoleByRoleName(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Role not found with name: {0}", roleName)));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDTO updateRole(RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(roleDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Role not found with ID: {0}", roleDTO.getRoleId())));
        existingRole.setName(roleDTO.getName());
        Role updatedRole = roleRepository.save(existingRole);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        var role = readRoleByRoleId(roleId);
        if(userRepository.existsAllByRoleRoleId(roleId)){
            throw new ForbiddenActionException(HttpStatus.FORBIDDEN, "Cannot delete role assigned to users");
        }
        roleRepository.deleteById(roleId);
       
        log.info("deleteRole end ok -  role: {}", role);
        log.trace("deleteRole end ok - role: {}", role);
    }
}