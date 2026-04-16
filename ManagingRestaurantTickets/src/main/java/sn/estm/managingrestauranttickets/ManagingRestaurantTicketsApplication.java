package sn.estm.managingrestauranttickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ManagingRestaurantTicketsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagingRestaurantTicketsApplication.class, args);
    }

}
