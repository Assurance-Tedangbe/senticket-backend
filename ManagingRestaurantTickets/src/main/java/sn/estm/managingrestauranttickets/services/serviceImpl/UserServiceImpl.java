/* UserServiceImpl class implementing the UserService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.exceptions.ForbiddenActionException;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.UserMapper;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.TicketRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserDTO createUser(UserDTO userDto) {

        log.info("Creating user with details: {}", userDto);

        // Checking if resource already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ForbiddenActionException(HttpStatus.FORBIDDEN,
                    "User with username: " + userDto.getUsername() + " already exists");
        }

        if (userDto.getPassword() == null || userDto.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "The password must contain at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        Role role = resolveRole(userDto.getRoleDTO());

        // L'inscription est publique : on interdit de s'auto-attribuer ADMIN
        if ("ADMIN".equals(role.getName())) {
            throw new ForbiddenActionException(HttpStatus.FORBIDDEN,
                    "Le rôle ADMIN ne peut pas être attribué à l'inscription");
        }
        // 2. Convertir UserDTO en entité User
        User user = userMapper.toEntity(userDto);
        // 3. Assigner explicitement le rôle à l'utilisateur
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));  //ICI
        // 4. Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        log.info("✅ User created successfully: {} (ID: {}, rôle: {})",
                savedUser.getUsername(), savedUser.getId(), role.getName());

        return userMapper.toDto(savedUser);
    }
    /// Vérifier si le rôle existe dans la BD
    private Role resolveRole(RoleDTO roleDTO) {
        if (roleDTO == null) {
            throw new IllegalArgumentException("Le rôle doit être fourni");
        }
        if (roleDTO.getId() != null) {
            return roleRepository.findById(roleDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Rôle non trouvé avec l'ID: " + roleDTO.getId()));
        }
        if (roleDTO.getName() != null && !roleDTO.getName().isBlank()) {
            return roleRepository.findByName(roleDTO.getName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Rôle non trouvé avec le nom: " + roleDTO.getName()));
        }
        throw new IllegalArgumentException("L'ID ou le nom du rôle doit être fourni");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO authenticate(String username, String password) {
        log.info("Tentative d'authentification pour l'utilisateur: {}", username);

        // Recherche de l'utilisateur
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Utilisateur non trouvé: {}", username);
                    return new ResourceNotFoundException(
                            MessageFormat.format("Utilisateur non trouvé: {0}", username)
                    );
                });

        // Vérification du mot de passe
        // Dans un système de production, utilisez BCryptPasswordEncoder !
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Mot de passe incorrect pour l'utilisateur: {}", username);
            throw new IllegalArgumentException("Mot de passe incorrect");
        }
        log.info("Authentification réussie pour: {}", username);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> readUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public UserDTO readUserByUserId(Long userId) {
        return userMapper.toDto(findUserOrThrow(userId));
    }
    @Override
    @Transactional(readOnly = true)
    public UserDTO readUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "User not found with username: {0}", username)));
        return userMapper.toDto(user);
    }
    @Transactional
    @Override
    public UserDTO updateUser(UserDTO userDto) {

        User existingUser = findUserOrThrow(userDto.getId());

        // Chargement et vérification username
        if (userDto.getUsername() != null
                && !userDto.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new IllegalArgumentException(
                        "Ce nom d'utilisateur est déjà utilisé");
            }
            existingUser.setUsername(userDto.getUsername());
        }

        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        // Le rôle et le mot de passe ne se modifient pas ici

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with username: {}", updatedUser.getUsername());
        return userMapper.toDto(updatedUser);
    }
    @Transactional
    @Override
    public void updatePassword(Long userId, String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caractères");
        }
        User user = findUserOrThrow(userId);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        log.info("Password updated for userId: {}", userId);
    }
    @Transactional
    @Override
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = findUserOrThrow(userId);
        user.setFcmToken(fcmToken);
        userRepository.save(user);
        log.info("✅ FCM token updated for : {}", user.getUsername());
    }
    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = findUserOrThrow(userId);
        // Can't delete user if they have tickets
        if (ticketRepository.existsByUser(user)) {
            throw new IllegalStateException(
                    "Impossible de supprimer un utilisateur qui possède des tickets");
        }
        userRepository.delete(user);
        log.info("User deleted - userId: {}", userId);
    }
    @Override
    public void logout(String username) {
        log.info("Déconnexion de : {}", username);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                        "User not found with ID: {0}", userId)));
    }
}

