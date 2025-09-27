/* UserServiceImpl class implementing the UserService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.RoleMapper;
import sn.estm.managingrestauranttickets.mappers.UserMapper;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserDTO createUser(UserDTO userDto) {
        log.info("Creating user with details: {}", userDto);
        
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getUserId());
       
        return userMapper.toDto(savedUser);
    }

    @Override
    public List<UserDTO> readUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {
        log.info("Updating user details: {}", userDto);

        User existingUser = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userDto.getUserId())));
        existingUser.setUsername(userDto.getUsername());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(roleMapper.toEntity(userDto.getRoleDTO()));
        
        User updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully with username: {}", updatedUser.getUsername());
        
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userId)));
        userRepository.delete(user);

        log.info("deleteUser end ok - userId: {}", userId);
    }

    @Override
    public void updatePassword(Long userId, String password) {
        log.info("Updating password for user with userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userId)));
        user.setPassword(password); // In a real application, ensure to hash the password before saving
        
        log.debug("Password updated for userId: {}", userId);
        
        userRepository.save(user);
    }

    @Override
    public UserDTO readUserByUserId(Long userId) {
        log.info("Reading user by userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userId)));
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO readUserByUsername(String username) {
        log.info("Reading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with username: {0}", username)));
        return userMapper.toDto(user);
    }

    @Override
    public void addRoleToUser(Long userId, Long roleId) {
        log.info("Adding role {} to user with userId: {}", roleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("User not found with ID: {0}", userId)));        

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format("Role not found with ID: {0}", roleId)));

        user.setRole(role);

        userRepository.save(user);

        log.info("Role {} added to user with userId: {}", roleId, userId);
    }
}
