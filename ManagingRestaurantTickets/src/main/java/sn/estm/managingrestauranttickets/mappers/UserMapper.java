package sn.estm.managingrestauranttickets.mappers;

import java.util.Set;

import org.mapstruct.Mapper;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.User;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    Set<UserDTO> toDtoSet(Set<User> users);

    Set<User> toEntitySet(Set<UserDTO> userDTOs);
}
