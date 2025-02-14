package com.bej.customer.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtFilter extends GenericFilter {




    /*
     * Override the doFilter method of GenericFilterBean.
     * Create HttpServletRequest , HttpServletResponse and ServletOutputStream object
     * Retrieve the "authorization" header from the HttpServletRequest object.
     * Retrieve the "Bearer" token from "authorization" header.
     * If authorization header is invalid, throw Exception with message.
     * Parse the JWT token and get claims from the token using the secret key
     * Set the request attribute with the retrieved claims
     * Call FilterChain object's doFilter() method */

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        ServletOutputStream pw = httpServletResponse.getOutputStream();
        // expects the token to come from header
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            //If token is not coming than set the response status as UNAUTHORIZED
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            pw.println("Missing or invalid Token ");
            pw.close();
        }else {
            String jwtToken = authHeader.substring(7);
            String customerName = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(jwtToken).getBody().getSubject();
            httpServletRequest.setAttribute("customername", customerName);
            // pass the claims in the request
            filterChain.doFilter(servletRequest, servletResponse); //some more filters , controller}


        }


        }
    }

