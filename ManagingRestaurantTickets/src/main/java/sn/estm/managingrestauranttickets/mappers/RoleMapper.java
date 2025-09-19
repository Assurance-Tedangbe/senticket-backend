/**
* Generates the RoleMapper interface corresponding to the Role class
**/

package sn.estm.managingrestauranttickets.mappers;

import org.mapstruct.Mapper;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.entities.Role;

/**
 * Mapper interface for converting between {@link Role} entities and {@link RoleDTO} data transfer objects.
 * This interface uses MapStruct to automatically generate the implementation at build time.
 * The {@code componentModel = "spring"} parameter allows the generated mapper to be injected as a Spring bean.

 * {@link #toRoleDTO(Role)}: Converts a {@link Role} entity to a {@link RoleDTO}
 * {@link #toRole(RoleDTO)}: Converts a {@link RoleDTO} to a {@link Role} entity
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface RoleMapper {

    RoleDTO toRoleDTO(Role role);

    Role toRole(RoleDTO roleDto);
}
