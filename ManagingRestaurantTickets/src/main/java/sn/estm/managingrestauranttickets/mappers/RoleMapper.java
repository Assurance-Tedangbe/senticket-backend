package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toRoleDTO(Role role);

    Role toRole(RoleDTO roleDto);

}
