package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.mappers.RoleMapper;
import sn.estm.managingrestauranttickets.mappers.UserMapper;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    /*
        Crud utilisateurs,
        Activer/Désactiver compte utilisateur
        addRoleToUser
        addAccountToUser
        majProfil
     */
    final UserRepository userRepository;
    final RoleRepository roleRepository;
    final UserMapper userMapper;
    final RoleMapper roleMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDto) {
        log.info("Creating user with details: {}", userDto);
        String pw=userDto.getPassword();
        userDto.setPassword(passwordEncoder.encode(pw));
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);

        return userMapper.toUserDTO(savedUser);

    }

    @Override
    public List<UserDTO> findAllUsers() {

        return userRepository.findAll().stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserDTO userDto) {

        log.info("Updating user details: {}", userDto);

        User user = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDto.getUsername());
        user.setUserFirstName(userDto.getUserFirstName());
        user.setUserLastname(userDto.getUserFirstName());
        user.setUserEmailAddress(user.getUserEmailAddress());
        User updatedUser = userRepository.save(user);

        return userMapper.toUserDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {

        log.info("Deleting user with userId: {}", userId);

        userRepository.deleteById(userId);
    }

    @Override
    public void updatePassword(Long userId, String password) {

        log.info("Updating password for user with userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(password); // Ideally, hash the password before saving
        userRepository.save(user);
    }

    @Override
    public UserDTO readUserByUserId(Long userId) {

        log.info("Reading user by userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserDTO(user);
    }

    @Override
    public UserDTO readUserByUsername(String username) {

        log.info("Reading user by username: {}", username);

        User user = userRepository.findByUsername(username);
               // .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserDTO(user);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {

        User user = userRepository.findByUsername(username);
              //  .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByRoleName(roleName);
             //   .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    //majProfilUser
}
