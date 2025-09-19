package sn.estm.managingrestauranttickets.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.estm.managingrestauranttickets.dto.UserDTO;
import sn.estm.managingrestauranttickets.services.serviceInterfaces.UserService;

import java.util.ArrayList;
import java.util.Collection;

import static org.hibernate.query.sqm.tree.SqmNode.log;

/**
 * This service will load user details from the database or any data source.
 * In other words, it allows Spring Security to authenticate a user by fetching
 * their credentials and roles (authorities).
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomUserDetailsService implements UserDetailsService {

    final UserService userService;

    /**
     * Takes the username of the user trying to log in and returns a UserDetails
     * object(which contains user information like username, password, and authorities.)
     * If the user is not found, it throws a UsernameNotFoundException
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Fetch user from the database using userService
        UserDTO userDTO = userService.readUserByUsername(username);
        log.info(username);
        //.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        userDTO.getRoles().forEach(r ->{
            authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
        });

        // Convert the user to UserDetails & the user to return is the spring security user format
        return  new User(userDTO.getUsername(), userDTO.getPassword(), authorities);
    }
}
