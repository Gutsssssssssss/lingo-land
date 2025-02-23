package com.ssafy.a603.lingoland.member.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private SecretKey encryptionkey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.encryptionkey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.ENC.A256CBC_HS512.key().build().getAlgorithm());
    }

    public String getLoginIdFromToken(String token) {
        return decryptPayload(token).get("loginId", String.class);
    }

    public Integer getMemberIdFromToken(String token) {
        return decryptPayload(token).get("memberId", Integer.class);
    }

    public String getCategoryFromToken(String token) {
        return decryptPayload(token).get("category", String.class);
    }

    // 유저가 필요한 권한이 있는지 체크할 때 사용
    public String getRoleFromToken(String token) {
        return decryptPayload(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return decryptPayload(token).getExpiration().before(new Date());
    }

    public String createToken(String category, String loginId, Integer memberId, String role, Long expiredMs) {
        return Jwts.builder()
                .header().add("typ", "JWT")
                .and()
                .claim("payload", encryptPayload(category, loginId, memberId, role, expiredMs))
                .signWith(secretKey)
                .compact();
    }

    private String encryptPayload(String category, String loginId, Integer memberId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("loginId", loginId)
                .claim("memberId", memberId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .encryptWith(encryptionkey, Jwts.ENC.A256CBC_HS512)
                .compact();
    }

    private Claims decryptPayload(String token) {
        String payload = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("payload", String.class);
        return Jwts.parser().decryptWith(encryptionkey).build().parseEncryptedClaims(payload).getPayload();
    }

}
