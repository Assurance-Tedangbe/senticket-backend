package sn.estm.managingrestauranttickets.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.User;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    /** Convert a User entity to a UserDTO.
     * This method maps the fields of the User entity to the corresponding fields
     * in the UserDTO.
     * @param user the User entity to convert
     * @return the corresponding UserDTO
     */
    @Mapping(source = "role", target = "roleDTO")
    UserDTO toDto(User user);

    /** Convert a UserDTO to a User entity.
     * This method maps the fields of the UserDTO to the corresponding fields
     * in the User entity.
     * @param userDTO the UserDTO to convert
     * @return the corresponding User entity
     */
    @Mapping(source = "roleDTO", target = "role")
    User toEntity(UserDTO userDTO);

    /** Convert a set of User entities to a set of UserDTOs.
     * This method is useful for mapping collections of users.
     *
     * @param users the set of User entities to convert
     * @return a set of UserDTOs
     */
    Set<UserDTO> toDtoSet(Set<User> users);

    Set<User> toEntitySet(Set<UserDTO> userDTOs);
}
