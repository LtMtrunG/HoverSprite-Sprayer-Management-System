package com.group12.springboot.hoversprite.jwt;

import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.config.CustomUserDetails;
import com.group12.springboot.hoversprite.config.CustomUserDetailsService;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.user.UserAPI;
import com.group12.springboot.hoversprite.user.UserAuthenticateDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final CustomJWTDecoder jwtDecoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT from cookies
        String jwt = getJwtFromCookies(request);
        System.out.println(jwt);
        if (jwt != null) {
            try {
                // Decode and validate JWT
                var jwtDecoded = jwtDecoder.decode(jwt);
                System.out.println("JWT Subject: " + jwtDecoded.getSubject());

                // Load user details from the decoded JWT
                Long userId = Long.parseLong(jwtDecoded.getSubject());
                CustomUserDetails userDetails = userDetailsService.loadUserById(userId);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                System.out.println(userDetails.getAuthorities().toString());

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
//                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
