/*
package sn.estm.managingrestauranttickets.security.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import sn.estm.managingrestauranttickets.constantes.JWTUtil;

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

    */
/**
     * This method executes when the user enters their username and password.
     * It gets the parameters from the request.
     * Then stores them in the UsernamePasswordAuthenticationToken object.
     * authenticate() is responsible for triggering the authentication operation
     * (calling userDetailsService, which calls the method that accesses DB,
     * retrieving the user, etc.)
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     *//*

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

    */
/**
     * When Spring Security calls successfulAuthentication, it passes an Authentication parameter
     * containing the authentication result. Now, we declare a Spring User object
     * that will be used to get the authenticated user(getPrincipal())
     * next it's to generate a jwt( jwtAccessToken) which expires within 5min
     * Issuer is the app's name that generated the token
     * get the list of roles(authorities) and convert it into a list of strings
     * sign the token with algo1
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     *//*

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("SuccessfulAuthentication");
        User user=(User) authResult.getPrincipal();
        Algorithm algo1=Algorithm.HMAC256(JWTUtil.SECRET);
        String jwtAccessToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JWTUtil.EXPIRE_ACCESS_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(ga-> ga.getAuthority()).collect(Collectors.toList()))
                .sign(algo1);

        // response.setHeader("Authorization",jwtAccessToken);

        String jwtRefreshToken= JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXPIRE_REFRESH_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .sign(algo1);
        Map<String, String> idToken=new HashMap<>();
        idToken.put("access-token",jwtAccessToken);
        idToken.put("refresh-token",jwtRefreshToken);
        response.setContentType("application/json"); //indicate to the client that the response body content contains json data
        new ObjectMapper().writeValue(response.getOutputStream(),idToken); //send the object in json format in the response body

        super.successfulAuthentication(request, response, chain, authResult);
    }


}
*/
