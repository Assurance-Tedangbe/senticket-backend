package sn.estm.managingrestauranttickets.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sn.estm.managingrestauranttickets.entities.Role;
import sn.estm.managingrestauranttickets.entities.User;
import sn.estm.managingrestauranttickets.repositories.RoleRepository;
import sn.estm.managingrestauranttickets.repositories.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminUser();
    }

    private void initRoles() {
        createRoleIfNotExists("ETUDIANT");
        createRoleIfNotExists("PORTIER");
        createRoleIfNotExists("ADMIN");
    }

    private void createRoleIfNotExists(String roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder().name(roleName).build();
            roleRepository.save(role);
            log.info("✅ Rôle créé : {}", roleName);
        } else {
            log.info("ℹ️ Rôle déjà existant : {}", roleName);
        }
    }

    private void initAdminUser() {
        if (userRepository.findByUsername("admin").isPresent()) {
            log.info("ℹ️ Utilisateur admin déjà existant");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rôle ADMIN introuvable"));

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@1234"))
                .firstName("Super")
                .lastName("Admin")
                .email("admin@senticket.sn")
                .role(adminRole)
                .build();

        userRepository.save(admin);
        log.info("✅ Utilisateur admin créé — username: admin / password: Admin@1234");
        log.warn("⚠️ Changez le mot de passe admin en production !");
    }
}