package com.bej.product.util;

import com.bej.product.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Service
public class JwtUtil {

    public String createToken(User user){
        // Create the token and add customer Id in the claims
        String jwtToken = null;


        jwtToken = Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256,"secretKey").compact();

        Map<String,String> map = new HashMap<>();
        map.put("token",jwtToken);
        map.put("message","User Successfully logged in");
        return jwtToken;

    }




}
