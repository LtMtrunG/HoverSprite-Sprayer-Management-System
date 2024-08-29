package com.group12.springboot.hoversprite.jwt;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class JwtUtils {

    // Extracting JWT from cookies
    public static String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null; // No JWT found
    }
}
