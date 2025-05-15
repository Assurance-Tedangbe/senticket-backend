package sn.estm.managingrestauranttickets.services;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*  Takes the username of the user trying to log in and returns a UserDetails
         object(which contains user information like username, password, and authorities.)
         If the user is not found, it throws a UsernameNotFoundException  */

        // Fetch user from the database using userService
        UserDTO userDTO = userService.readUserByUsername(username);
                //.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        userDTO.getRoles().forEach(r ->{
            authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
        });

        // Convert the user to UserDetails
        return  new User(userDTO.getUsername(), userDTO.getPassword(), authorities);
        //the user to return is the spring security user format
    }
}
