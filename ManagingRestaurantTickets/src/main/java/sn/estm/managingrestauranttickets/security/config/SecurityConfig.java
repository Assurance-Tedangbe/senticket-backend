package sn.estm.managingrestauranttickets.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import sn.estm.managingrestauranttickets.security.jwt.filter.JwtFilter;
import sn.estm.managingrestauranttickets.services.CustomUserDetailsService;

@Configuration
//@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private JwtFilter jwtFilter;

   /* public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.customUserDetailsService = userDetailsService;
    }*/

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
          security management via HttpSecurity
        */
        http
                //.csrf(csrf -> csrf.disable())
                .csrf(AbstractHttpConfigurer::disable)
                // protection CSRF desactivation

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Desactivate session management (utile for JWT)

                .userDetailsService(customUserDetailsService)
                // Register the customUserDetailsService

                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()))

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/authenticate/login").permitAll()
                                .requestMatchers("/api/menus/**").permitAll()
                                .requestMatchers( new AntPathRequestMatcher("/api/roles/**")).hasRole("ADMIN")
                                .requestMatchers( new AntPathRequestMatcher("/api/users/**")).hasRole("ADMIN")
                                .requestMatchers( new AntPathRequestMatcher("/api/users/{userId}/profil")).hasAnyRole("ADMIN", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/comptes/**")).hasAnyRole("ADMIN", "AGENT", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/credits/**")).hasAnyRole("ADMIN", "AGENT", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/tickets/**")).hasAnyRole("ADMIN", "ETUDIANT")
                                .requestMatchers( new AntPathRequestMatcher("/api/debits/**")).hasAnyRole("ADMIN", "PORTIER")
                                .anyRequest().authenticated()
                        // Toute autre requête emise vers l'appli doit être authentifiée

                )

               // .addFilter(new JwtAuthenticationFilter(authenticationManagerBean()))
              //  .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
              /*  jwtFilter is your custom filter that checks for a Bearer token
                in the header and sets the user context.*/


              //  .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
               /* .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));*/

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); // uses your userDetailsService + PasswordEncoder automatically
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password encryption, should be called later
    }

   /* @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new
                DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }*/

}
