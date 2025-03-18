package sn.estm.managingrestauranttickets.security;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
public class SecurityConfig{

   /* final RestTemplateBuilder restTemplateBuilder;

    final SecurityOauth2Properties securityOauth2Properties;

    final AbstractRoleService abstractRoleService;*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

       /* JwtIssuerAuthenticationManagerResolver jwtIssuerAuthenticationManagerResolver = new JwtIssuerAuthenticationManagerResolver(
                new CustomTrustedIssuerJwtAuthenticationManagerResolver(
                        Collections.unmodifiableCollection(securityOauth2Properties.issuerUris())::contains,
                        restTemplateBuilder, securityOauth2Properties.proxyEnabled(),
                        securityOauth2Properties.proxyHost(), securityOauth2Properties.proxyPort(),
                        new GrantedAuthoritiesExtractor(abstractRoleService)));*/

        /*
          La gestion de la sécurité  via HttpSecurity
        */
        http
              //  .csrf(csrf -> csrf.disable())
                .csrf(AbstractHttpConfigurer::disable)
                // Désactive la protection CSRF    OK

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Désactive la gestion de session (utile pour JWT)  OK

                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()))  // OK

                //   .formLogin(withDefaults())   // Active l'authentification via formulaire

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/menus/**").permitAll()
                                .requestMatchers( new AntPathRequestMatcher("/api/roles/**")).hasRole("ADMIN")
                                .requestMatchers( new AntPathRequestMatcher("/api/users/**")).hasRole("ADMIN")
                                .requestMatchers( new AntPathRequestMatcher("/api/users/{userId}/profil")).hasAnyRole("ADMIN", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/comptes/**")).hasAnyRole("ADMIN", "AGENT", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/credits/**")).hasAnyRole("ADMIN", "AGENT", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/tickets/**")).hasAnyRole("ADMIN", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/debits/**")).hasAnyRole("ADMIN", "PORTIER")
                                .anyRequest().authenticated()
                        // Toute autre requête emise vers l'appli doit être authentifiée  OK

                )
               // .addFilter(new JwtAuthenticationFilter(authenticationManagerBean()))
               // .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)

                .httpBasic(Customizer.withDefaults())   // Active l'authentification Basic

              //  .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
               /* .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));*/

                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        // Définit l'URL de déconnexion

                        .logoutSuccessUrl("/public") // Redirection après déconnexion

                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

     /*
        La gestion des utilisateurs se fait via  UserDetailsService avec @Bean
     */

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }


}
