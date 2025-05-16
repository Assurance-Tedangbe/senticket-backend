package sn.estm.managingrestauranttickets.security.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sn.estm.managingrestauranttickets.services.CustomUserDetailsService;
import sn.estm.managingrestauranttickets.services.JwtService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    /**
     * It checks incoming HTTP requests for a JWT token,
     * extracts and validates it from the Authorization header, and if valid,
     * sets the authenticated user in Spring Security’s context so the rest of
     * the application knows the user is authenticated.
     */

    /* OncePerRequestFilter ensures this filter runs once per HTTP request */

    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * This method contains the actual filtering logic that runs for every request
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        /* Retrieves the Authorization header  */
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        /* Verifies that the header exists and starts with "Bearer ".
           Extracts the token and retrieves the username from it   */
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtService.extractUsername(jwt);
        }

        /* Continues only if: a username was extracted from the token.
           The user is not already authenticated (prevents redundant authentication) */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            /* Loads full user details from your database or custom auth service  */
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            /* Verifies the token's validation:
               correct signature, not expired, matches the username  */
            if (jwtService.validateToken(jwt, userDetails)) {

                /* Creates an Authentication object for Spring Security  */
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                /* Adds extra request info (e.g. IP address).   */
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                /* Stores the Authentication in the SecurityContext,
                   This tells Spring: "This user is now authenticated for this request*/
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }
        /* Continues the filter chain — passing the request to the next filter or controller  */
        filterChain.doFilter(request, response);
    }
}
