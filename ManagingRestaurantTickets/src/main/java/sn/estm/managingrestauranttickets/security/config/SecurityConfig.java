// Fichier: src/main/java/sn/estm/managingrestauranttickets/security/config/SecurityConfig.java
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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/payments/webhook", "/api/payments/return", "/api/payments/cancel").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/*").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ADMIN, ETUDIANT, PORTIER")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority("ADMIN, ETUDIANT, PORTIER")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/tickets/purchase").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/transferTickets").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/cancelTransfer").hasAuthority("ETUDIANT")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/debit").hasAuthority("PORTIER")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/statistics").hasAnyAuthority("ADMIN", "ETUDIANT")

                        .requestMatchers(HttpMethod.POST, "/api/payments/initiate").hasAuthority("ETUDIANT")

                        .anyRequest().authenticated()  // Tte autre requête emise vers l'appli doit être authentifiée
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}