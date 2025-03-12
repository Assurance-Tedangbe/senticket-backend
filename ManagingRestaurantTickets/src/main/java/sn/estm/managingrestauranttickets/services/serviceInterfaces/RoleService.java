package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.RoleDTO;

import java.util.List;

public interface RoleService {

    RoleDTO createRole(RoleDTO roleDTO);

    List<RoleDTO> findAllRoles();

    RoleDTO readRoleByRoleId(Long roleId);

    RoleDTO readRoleByRoleName(String roleName);

    RoleDTO updateRole(RoleDTO roleDTO);

    void deleteRole(Long roleId);

}
