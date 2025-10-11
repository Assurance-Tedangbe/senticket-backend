package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;

import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import org.mapstruct.Mapping;
import java.util.Set;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface RoleMapper {

   // @Mapping(source = "users", target = "userDTO")
    RoleDTO toDto(Role role);

  //  @Mapping(target = "users", ignore = true)
    Role toEntity(RoleDTO roleDTO);

    Set<RoleDTO> toDtoSet(Set<Role> roles);

    Set<Role> toEntitySet(Set<RoleDTO> roleDTOs);
}
