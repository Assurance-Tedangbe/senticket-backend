/*
package sn.estm.managingrestauranttickets.security.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.security.web.SecurityFilterChain;
import sn.estm.managingrestauranttickets.security.jwt.filter.JwtAuthenticationFilter;
import sn.estm.managingrestauranttickets.security.jwt.filter.JwtFilter;
import sn.estm.managingrestauranttickets.services.CustomUserDetailsService;

*/
/**
 * Set up the SecurityFilterChain to secure endpoints and integrate the JWT filter.
 *//*


@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig{

    final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtFilter jwtFilter) throws Exception {

        */
/*  security management via HttpSecurity  *//*

        http
                //.csrf(csrf -> csrf.disable())
                .csrf(AbstractHttpConfigurer::disable)

                //.formLogin(withDefaults()) // disable it when using jwt

                // Desactivate session management (utile for JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // customUserDetailsService registration
                .userDetailsService(customUserDetailsService)

                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()))

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/authenticate/login").permitAll()
                                .requestMatchers("/api/menus/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/roles/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/api/roles/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/api/roles/**").hasAuthority("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/roles/**").hasAuthority("ADMIN")
                                //.requestMatchers( new AntPathRequestMatcher("/api/roles/**")).hasRole("ADMIN")
                                //.requestMatchers( new AntPathRequestMatcher("/api/users/**")).hasRole("ADMIN")
                                //.requestMatchers( new AntPathRequestMatcher("/api/users/{userId}/profil")).hasAnyRole("ADMIN", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/comptes/**")).hasAnyRole("ADMIN", "AGENT", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/credits/**")).hasAnyRole("ADMIN", "AGENT", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/tickets/**")).hasAnyRole("ADMIN", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/debits/**")).hasAnyRole("ADMIN", "PORTIER")
                        //  .anyRequest().authenticated()
                        // Toute autre requête emise vers l'appli doit être authentifiée
                )
                // .addFilter(new JwtAuthenticationFilter(authenticationManagerBean())) // from videos
                */
/*  jwtFilter is your custom filter that checks for a Bearer token
                    in the header and sets the user context  *//*

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    */
/**
     * with bean, I have the possibility to inject this object where I want
     * @param authConfig
     * @return
     * @throws Exception
     *//*

    @Bean
    public AuthenticationManager authenticationManagerBean(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
        // return super.authenticationManagerBean(); //from videos
        */
/*  Spring auto-registers DaoAuthenticationProvider and uses
             userDetailsService + PasswordEncoder automatically      *//*

    }

    // this figure in video screenshot
   */
/* @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }*//*


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password encryption, should be called later
    }

    */
/* @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new
                DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }*//*


}
*/
