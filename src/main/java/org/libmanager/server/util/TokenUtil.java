package org.libmanager.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.crypto.SecretKey;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.lang.Maps;
import io.jsonwebtoken.security.Keys;

public class TokenUtil {

    private static final String signatureKey;

    // Get the signature key
    static {
        Properties applicationProperties = new Properties();
        InputStream is = TokenUtil.class.getResourceAsStream("/application.properties");
        try {
            applicationProperties.load(is);
            is.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        signatureKey = applicationProperties.getProperty("signaturekey");
    }

    /**
     * Check if a token is linked to an admin account
     * @param token The token to analyze
     * @return      True if the owner of the token is admin, false otherwise
     * */
    public static boolean isAdmin(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getEncoder().encode(
                        signatureKey.getBytes()
                )
        );

        try {
            Jwts.parserBuilder()
                .require("admin", true)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch (JwtException throwable) {
            System.out.println(throwable.getMessage());
            return false;
        }
    }

    /**
     * Check if a token is valid
     * @param token The token to analyze
     * @return True if the token is valid, false otherwise
     * */
    public static boolean isValid(String token) {

        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getEncoder().encode(
                        signatureKey.getBytes()
                )
        );

        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch (JwtException throwable) {
            System.out.println(throwable.getMessage());
            return false;
        }
    }

    /**
     * Check if the token is a token sent by mail
     * @param token The token to analyze
     * @return      True if the token is a token sent by mail
     */
    public static boolean isMailToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getEncoder().encode(
                        signatureKey.getBytes()
                )
        );

        try {
            Jwts.parserBuilder()
                .require("email", true)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch (JwtException throwable) {
            System.out.println(throwable.getMessage());
            return false;
        }
    }

    /**
     * Extract the username stored in the token
     * @param token The token to analyze
     * @return      The username stored in the token
     */
    public static String extractUsername(String token) {

        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getEncoder().encode(
                        signatureKey.getBytes()
                )
        );

        // Unsafe but ok according to the doc
        // https://github.com/jwtk/jjwt#json-custom
        JacksonDeserializer mapper = new JacksonDeserializer(Maps.of("username", String.class).build());

        return Jwts.parserBuilder()
                   .deserializeJsonWith(mapper)
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .get("username", String.class);
    }

    /**
     * Generate a token for a given user
     * @param username  The username that will be stored in the token
     * @param admin     The status that will be stored in the token
     * @return          A valid token
     * */
    public static String generateToken(String username, boolean admin) {

        // Get time + 1 hours
        LocalDateTime expirationDate = LocalDateTime.now().plusHours(1);

        // Generate => 256 bits long key from signature key in properties
        SecretKey key = Keys.hmacShaKeyFor(
                Base64.getEncoder().encode(
                        signatureKey.getBytes()
                )
        );

        // Generate token with an expiration date
        return Jwts.builder()
                   .setExpiration(
                           Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant())
                   )
                   .claim("username",username)
                   .claim("admin", admin)
                   .signWith(key)
                   .compact();
    }

    /**
     * Create a mail token for the user who requested a password reset
     * @param username  The user who requested a password reset
     * @return          The token
     * */
    public static String generateMailToken(String username) {
        if (signatureKey != null) {
            // Generate => 256 bits long key from signature key in properties
            SecretKey key = Keys.hmacShaKeyFor(
                    Base64.getEncoder().encode(
                            signatureKey.getBytes()
                    )
            );

            // Generate token with an expiration date
            return Jwts.builder()
                       .setExpiration(
                               // Token expires after 15 minutes
                               new Date(System.currentTimeMillis() + 60000 * 15)
                       )
                       .claim("username", username)
                       .claim("email", true)
                       .signWith(key)
                       .compact();
        }
        return null;
    }

}
