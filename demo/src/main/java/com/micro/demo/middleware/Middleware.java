package com.micro.demo.middleware;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.micro.demo.helpers.Helpers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class Middleware implements HandlerInterceptor {

    private static final List<String> NO_AUTH_PATHS = Arrays.asList("/login", "/register");
    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String tokenHeader = request.getHeader("Authorization");
        String requestPath = request.getRequestURI();

        if (NO_AUTH_PATHS.contains(requestPath)) {
            return true; // Allow access to excluded paths
        }

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            Helpers.log("error", "In Server: No token bearer provided");
            Map<String, Object> resp = Helpers.MsgErr(401, "In Server: No token bearer provided", tokenHeader);
            Helpers.Response(response, 401, resp);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String tokenPart = tokenHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenPart).getBody();

            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                Helpers.log("error", "In Server: Token is expired");
                Map<String, Object> resp = Helpers.MsgErr(401, "In Server: Authorization Failed", "Token is expired");
                Helpers.Response(response, 401, resp);
                return false;
            }

            request.setAttribute("email", claims.get("email"));
            return true;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    public static String createToken(String id, String username, String email) {
        Date expiration = new Date(System.currentTimeMillis() + 60 * 60 * 1000); 
        return Jwts.builder()
                .claim("authorized", true)
                .claim("id", id)
                .claim("username", username)
                .claim("email", email)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
