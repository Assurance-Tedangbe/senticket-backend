/**
* Generates the UserMapper interface corresponding to the User class
**/

package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.User;

/**
 * Mapper interface for converting between {@link User} entities and {@link UserDTO} data transfer objects.
 * This interface uses MapStruct to automatically generate the implementation at build time.
 * The {@code componentModel = "spring"} parameter allows the generated mapper to be injected as a Spring bean.

 * {@link #toUserDTO(User)}: Converts a {@link User} entity to a {@link UserDTO}
 * {@link #toUser(UserDTO)}: Converts a {@link UserDTO} to a {@link User} entity
 */
@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {
    UserDTO toUserDTO(User user);

    User toUser(UserDTO userDTO);
}





