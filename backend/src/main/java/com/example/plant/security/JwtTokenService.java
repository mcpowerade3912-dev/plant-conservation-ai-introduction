package com.example.plant.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets; import java.util.*;
@Service public class JwtTokenService { private final String secret; private final long expiration;
 public JwtTokenService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-seconds}") long expiration){this.secret=secret;this.expiration=expiration;}
 public String create(String username,String role){long now=System.currentTimeMillis();return Jwts.builder().setSubject(username).claim("role",role).setIssuedAt(new Date(now)).setExpiration(new Date(now+expiration*1000)).signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS256).compact();}
 public Claims parse(String token){return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))).build().parseClaimsJws(token).getBody();}
}
