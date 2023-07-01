package com.emsh.taskgroup.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    // Expiración del jwt expresada en milisegundos (1 día)
    private static final long EXPIRATION_MILLIS = 86400000;

    /**
     * Extrae el username (email) contenido en el PayLoad del token jwt
     * @param jwt: token jwt recibido por el header del request
     * @return el username (email) correspondiente al subject del token jwt proporcionado
     */
    public String extractUserName(String jwt) {
        return extractSingleClaim(jwt, Claims::getSubject);
    }

    /**
     * Genera el token jwt para el usuario proporcionado
     * @param userDetails: userDetails del usuario al cual se le quiere generar el token jwt
     * @return token jwt generado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String userName = extractUserName(jwt);
        if (isTokenExpired(jwt))
            return false;
        return (userName.equals(userDetails.getUsername()));
    }

    /**
     * Verifica que el token jwt no haya expirado
     * @param jwt: token jwt que se desea validar
     * @return true si el token expiró, false caso contrario
     */
    public boolean isTokenExpired(String jwt) {
        final Date dateExpiration = extractSingleClaim(jwt, Claims::getExpiration);
        return dateExpiration.before(new Date(System.currentTimeMillis()));

    }

    private String generateToken(Map<String, Objects> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractSingleClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) {
         return Jwts
                 .parserBuilder()
                 .setSigningKey(getSigningKey())
                 .build()
                 .parseClaimsJws(jwt)
                 .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
