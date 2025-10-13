package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;

import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDto(Role role);

    Role toEntity(RoleDTO roleDTO);

    Set<RoleDTO> toDtoSet(Set<Role> roles);

    Set<Role> toEntitySet(Set<RoleDTO> roleDTOs);
}
