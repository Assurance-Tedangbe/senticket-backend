package sn.estm.managingrestauranttickets.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import sn.estm.managingrestauranttickets.security.jwt.AuthEntryPointJwt;
import sn.estm.managingrestauranttickets.security.jwt.CustomAccessDeniedHandler;
import sn.estm.managingrestauranttickets.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthEntryPointJwt authEntryPointJwt;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authEntryPointJwt)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        /// ============ OPTIONS (pre-flight CORS) ============
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        /// ============ AUTHENTIFICATION ============
                       // .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()   // login : public
                        .requestMatchers("/api/auth/**").authenticated()   // tout le reste (validate, me, logout) : connecté

                        /// ============ UTILISATEURS ============
                        // Inscription libre
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // Liste complète : ADMIN seulement
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")

                        // Un utilisateur connecté peut voir/modifier son propre profil
                        // (contrôleur vérifie que c'est son propre compte : userId == utilisateur connecté)
                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/username/{username}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/password/{userId}").authenticated()
                        // Suppression : ADMIN seulement
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasAuthority("ADMIN")

                        /// ============ RÔLES ============
                        .requestMatchers(HttpMethod.GET, "/api/roles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/**").hasAuthority("ADMIN")

                        /// ============ TICKETS ============
                        .requestMatchers(HttpMethod.GET, "/api/tickets").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tickets/user/{userId}/purchased").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tickets/user/{userId}/filter").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tickets/statistics").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/purchase").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/transferTickets").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/cancelTransfer").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/debit").hasAuthority("PORTIER")

                        /// ============ PAIEMENTS ============
                        /// Callbacks PayDunya : publics (appelés par PayDunya sans token)
                        .requestMatchers("/api/payments/webhook").permitAll()
                        .requestMatchers("/api/payments/return").permitAll()
                        .requestMatchers("/api/payments/cancel").permitAll()
                        /// Actions Flutter : authentifiées
                        .requestMatchers(HttpMethod.POST, "/api/payments/initiate").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.GET, "/api/payments/status/**").hasAuthority("ETUDIANT")

                        /// ============ FILET DE SÉCURITÉ ============
                        /// Toute route non listée explicitement exige d'être connecté
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}