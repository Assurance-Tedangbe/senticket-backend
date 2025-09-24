package sn.estm.managingrestauranttickets.services.serviceInterfaces;

import sn.estm.managingrestauranttickets.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO userDto);

    List<UserDTO> readUsers();

    UserDTO updateUser(UserDTO userDto);

    void deleteUser(Long userId);

    void updatePassword(Long userId, String password);

    UserDTO readUserByUserId(Long userId);

    UserDTO readUserByUsername(String username);

    void addRoleToUser(String username, String roleName);

}
