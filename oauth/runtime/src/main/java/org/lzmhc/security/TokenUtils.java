package org.lzmhc.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.time.LocalDateTime;
import java.util.*;

@Singleton
public class TokenUtils {
    @Inject
    JsonWebToken jwt;
    public JsonWebToken getJwt() {
        return jwt;
    }
    //生成token
    public String generateToken(String userName, String email, String avator) throws Exception {
        return Jwt
                .subject(email)
                .upn(userName)
                .claim("avator", avator)
                .claim(Claims.birthdate.name(), LocalDateTime.now())
                .sign();
    }
    //获取claim信息
    public Map<String, Object> getClaimByName() {
        Map<String, Object> claims = new HashMap<>();
        Set<String> claimNames = jwt.getClaimNames();
        for (String claimName : claimNames) {
            claims.put(claimName, jwt.getClaim(claimName));
        }
        return claims;
    }
    //获取用户唯一标识
    public String getUserId() {
        return jwt.getSubject();
    }
    //注销token
    public Boolean logout(){
        return null;
    }
}
