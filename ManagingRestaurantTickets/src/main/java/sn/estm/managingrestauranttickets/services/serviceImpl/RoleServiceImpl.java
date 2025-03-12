package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.mappers.RoleMapper;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleServiceImpl implements RoleService {

    final RoleRepository roleRepository;
    final RoleMapper roleMapper;
    final UserRepository userRepository;


    @Override
    public RoleDTO createRole(RoleDTO roleDto) {

        log.info("Creating role with details: {}", roleDto);

        Role role = roleMapper.toRole(roleDto);
        Role savedRole = roleRepository.save(role);

        return roleMapper.toRoleDTO(savedRole);
    }

    @Override
    public List<RoleDTO> findAllRoles() {

        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleDTO)
                .collect(Collectors.toList());

    }

    @Override
    public RoleDTO readRoleByRoleId(Long roleId) {

        log.info("readRoleByRoleId end ok - roleId: {}", roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        return roleMapper.toRoleDTO(role);
    }

    @Override
    public RoleDTO readRoleByRoleName(String roleName) {

        Role role = roleRepository.findByRoleName(roleName);
             //   .orElseThrow(() -> new RuntimeException("Role not found"));
        log.info("readRoleByRoleName end ok - roleName: {}", roleName);
        log.trace("get role by name was ok - role: {}", role);

        return roleMapper.toRoleDTO(role);

    }

    @Override
    public RoleDTO updateRole(RoleDTO roleDTO) {

        Role role = roleRepository.findById(roleDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        role.setRoleName(roleDTO.getRoleName());
        Role updatedRole = roleRepository.save(role);

        log.info("updateRole end ok -  name: {}", roleDTO.getRoleName());
        log.trace("updateRole end ok - roles: {}", updatedRole);

        return roleMapper.toRoleDTO(updatedRole);

    }

    @Override
    public void deleteRole(Long roleId) {

        roleRepository.deleteById(roleId);

        log.info("deleteRole end ok -  roleId: {}", roleId);
        log.trace("deleteRole end ok - roleId: {}", roleId);
    }
}
