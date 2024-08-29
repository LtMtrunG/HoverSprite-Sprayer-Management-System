package com.group12.springboot.hoversprite.config;

import com.group12.springboot.hoversprite.exception.CustomAuthenticationEntryPoint;
import com.group12.springboot.hoversprite.jwt.CustomJWTDecoder;
import com.group12.springboot.hoversprite.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.group12.springboot.hoversprite.authentication.CustomAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINT = { "/register/farmer/**", "/auth/login", "/auth/introspect", "/auth/logout",
            "/**", "/login/**", "/oauth2/**"};
    private final String[] STATIC_RESOURCES = { "/assets/**", "/index.html" };

    @Autowired
    private CustomJWTDecoder customJWTDecoder;
    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(PUBLIC_ENDPOINT).permitAll()
                .requestMatchers(STATIC_RESOURCES).permitAll()
                .anyRequest().authenticated()).oauth2Login(oauth2 -> oauth2
                        .successHandler(successHandler)
                         .defaultSuccessUrl("/login.html", true) // Redirect after successful login
                         .failureUrl("/login?error") // Redirect in case of failure
                );
        // Register the JWT Token Filter
        httpSecurity.addFilterBefore(new JwtTokenFilter(customJWTDecoder, userDetailsService), UsernamePasswordAuthenticationFilter.class);
//        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(customJWTDecoder)
//                .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        httpSecurity.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}