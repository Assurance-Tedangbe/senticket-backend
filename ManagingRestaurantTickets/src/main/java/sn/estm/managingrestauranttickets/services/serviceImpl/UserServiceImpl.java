/* UserServiceImpl class implementing the UserService interface. */
package sn.estm.managingrestauranttickets.services.serviceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import sn.estm.managingrestauranttickets.dto.RoleDTO;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.dto.customisedto.CreationTicketsRequestDTO;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.entities.Ticket;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.enumerations.TicketStatus;
import sn.estm.managingrestauranttickets.enumerations.TicketType;
import sn.estm.managingrestauranttickets.exceptions.InvalidCredentialsException;
import sn.estm.managingrestauranttickets.exceptions.ResourceNotFoundException;
import sn.estm.managingrestauranttickets.mappers.TicketMapper;
import sn.estm.managingrestauranttickets.mappers.UserMapper;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.TicketRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.TicketService;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
   // private final UserService userService;


   /* @Override
    public UserDTO createUser(UserDTO userDto) {

        log.info("Creating user with details: {}", userDto);
        
        User user = userMapper.toEntity(userDto);
        
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getUserId());
       
        return userMapper.toDto(savedUser);
    }*/
    @Transactional
    @Override
    public UserDTO createUser(UserDTO userDto) {

        log.info("Creating user with details: {}", userDto);

        // Vérifier que le rôle est fourni
        if (userDto.getRoleDTO() == null) {
            throw new IllegalArgumentException("Le rôle est obligatoire pour créer un utilisateur");
        }

        // 1. Vérifier si le rôle existe dans la base de données
        RoleDTO roleDto = userDto.getRoleDTO();
        Role role = roleRepository.findById(roleDto.getRoleId())
                .orElseThrow(() -> new RuntimeException(
                        "Rôle non trouvé avec l'ID: " + roleDto.getRoleId()
                ));

        log.info("Rôle trouvé: {} (ID: {})", role.getName(), role.getRoleId());

        // 2. Convertir UserDTO en entité User
        User user = userMapper.toEntity(userDto);

        // 3. Assigner explicitement le rôle à l'utilisateur
        user.setRole(role);

        // 4. Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getUserId());

        // 5. Convertir en DTO pour la réponse
        UserDTO savedUserDto = userMapper.toDto(savedUser);

        log.info("User created successfully with ID: {}", savedUserDto.getUserId());

        //var userDTO = userService.readUserByUserId(userDto.getUserId());
        //**************************************
        log.info("BEGIN BUILDING TICKETS:");
        CreationTicketsRequestDTO creationTicketsRequestDTO = CreationTicketsRequestDTO.builder()
                .userDTO(userDto)
                .countA(5)
                .countB(5)
                .build();

        /*log.info("Deep: Creating tickets with details {}", creationTicketsRequestDTO);*/

        if (creationTicketsRequestDTO.getCountA() < 0 || creationTicketsRequestDTO.getCountB() < 0) {
            throw new IllegalArgumentException("Ticket counts cannot be negative");
        }

        List<Ticket> ticketsToSave = new ArrayList<>();
        LocalDateTime creationTime = LocalDateTime.now();

        // Create Type A tickets
        for (int i = 0; i < creationTicketsRequestDTO.getCountA(); i++) {
            Ticket ticketA = Ticket.builder()
                    .ticketType(TicketType.A)
                    .ticketPrice(100.0)
                    .payementCode("")
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .booked(false)
                    .ticketCreationDate(creationTime)
                    .ticketDescription("Ticket Type A - " + (i + 1))
                    .build();
            ticketsToSave.add(ticketA);
        }

        // Create Type B tickets
        for (int i = 0; i < creationTicketsRequestDTO.getCountB(); i++) {
            Ticket ticketB = Ticket.builder()
                    .ticketType(TicketType.B)
                    .ticketPrice(150.0)
                    .payementCode("")
                    .ticketStatus(TicketStatus.AVAILABLE)
                    .booked(false)
                    .ticketCreationDate(creationTime)
                    .ticketDescription("Ticket Type B - " + (i + 1))
                    .build();
            ticketsToSave.add(ticketB);
        }

        // Use saveAll for efficient batch insertion
        List<Ticket> savedTickets = ticketRepository.saveAll(ticketsToSave);
        log.info("*****savetickets****** "+savedTickets);


        creationTicketsRequestDTO.setTicketDTO(ticketMapper.toDtoSet(savedTickets));
        log.info("********creationTicketsRequests********* "+creationTicketsRequestDTO);
        log.info("ENDING BUILDING TICKETS:");

        //******************************
        log.info("Deep: Creating tickets with details {}", creationTicketsRequestDTO);

        ticketService.createTickets(creationTicketsRequestDTO);

        log.info("Successfully created tickets {} with requests {}",
                savedTickets.size(), creationTicketsRequestDTO);

        return savedUserDto;
    }

    @Override
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
        // ⚠️ IMPORTANT : Dans un système de production, utilisez BCryptPasswordEncoder !
        if (!user.getPassword().equals(password)) {
            log.warn("Mot de passe incorrect pour l'utilisateur: {}", username);
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        log.info("Authentification réussie pour: {}", username);
        return userMapper.toDto(user);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        log.info("Validation des identifiants pour: {}", username);

        try {
            User user = userRepository.findByUsername(username)
                    .orElse(null);

            if (user == null) {
                log.warn("Utilisateur non trouvé: {}", username);
                return false;
            }

            // Vérification du mot de passe
            boolean isValid = user.getPassword().equals(password);

            if (!isValid) {
                log.warn("Mot de passe incorrect pour: {}", username);
            } else {
                log.info("Identifiants valides pour: {}", username);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Erreur lors de la validation des identifiants: {}", e.getMessage());
            return false;
        }
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
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", userDto.getUserId())));
                    
        existingUser.setUsername(userDto.getUsername());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        
        User updatedUser = userRepository.save(existingUser);

        log.info("User updated successfully with username: {}", updatedUser.getUsername());
        
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {

        log.info("Deleting user with userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", userId)));
        userRepository.delete(user);

        log.info("deleteUser end ok - userId: {}", userId);
    }

    @Override
    public void updatePassword(Long userId, String password) {

        log.info("Updating password for user with userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", userId)));
                    
        user.setPassword(password); // In a real application, ensure to hash the password before saving
        
        log.debug("Password updated for userId: {}", userId);
        
        userRepository.save(user);
    }

    @Override
    public UserDTO readUserByUserId(Long userId) {

        log.info("Reading user by userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", userId)));
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO readUserByUsername(String username) {

        log.info("Reading user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with username: {0}", username)));
        return userMapper.toDto(user);
    }

    @Override
    public void addRoleToUser(Long userId, Long roleId) {

        log.info("Adding role {} to user with userId: {}", roleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", userId)));        

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Role not found with ID: {0}", roleId)));

        user.setRole(role);

        userRepository.save(user);

        log.info("Role {} added to user with userId: {}", roleId, userId);
    }


    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {

        log.info("Removing role {} from user with userId: {}", roleId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "User not found with ID: {0}", userId)));        

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageFormat.format(
                    "Role not found with ID: {0}", roleId)));

        if (user.getRole() == null || !user.getRole().equals(role)) {
            throw new IllegalArgumentException("User does not have the specified role.");
        }

        user.setRole(null);

        userRepository.save(user);

        log.info("Role {} removed from user with userId: {}", roleId, userId);
    }

    @Override
    public void scanCodeQr(UserDTO userDto) {
        
    }
}
