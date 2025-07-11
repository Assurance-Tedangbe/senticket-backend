package sn.estm.managingrestauranttickets.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jwt.JWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sn.estm.managingrestauranttickets.entities.Role;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username);
        System.out.println(password);
        UsernamePasswordAuthenticationToken authenticationToken=
                new UsernamePasswordAuthenticationToken(username,password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("SuccessfulAuthentication");
        User user=(User) authResult.getPrincipal();
        Algorithm alogo1=Algorithm.HMAC256("mysecret1234");
        String jwtAccessToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+5*60*100))
                .withIssuer(request.getRequestId().toString())
                .withClaim("roles", user.getAuthorities().stream().map(ga-> ga.getAuthority()).collect(Collectors.toList()))
                .sign(alogo1);

        String jwtRefreshToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+15*60*100))
                .withIssuer(request.getRequestURL().toString())
               // .withClaim("roles", user.getAuthorities().stream().map(ga-> ga.getAuthority()).collect(Collectors.toList()))
                .sign(alogo1);
        Map<String, String> idToken=new HashMap<>();
        idToken.put("access-token",jwtAccessToken);
        idToken.put("refresh-token",jwtRefreshToken);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),idToken);

        response.setHeader("Authorization",jwtAccessToken);

       super.successfulAuthentication(request, response, chain, authResult);
    }


}
