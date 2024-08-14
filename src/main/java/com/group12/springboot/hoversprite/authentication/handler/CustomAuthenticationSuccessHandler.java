package com.group12.springboot.hoversprite.authentication.handler;

import jakarta.servlet.ServletException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomAuthenticationSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
            Object principal = oauth2Auth.getPrincipal();

            // Handle OIDC users
            if (principal instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) principal;
                String idToken = oidcUser.getIdToken().getTokenValue();
                System.out.println("ID Token: " + idToken);

                // Optional: Redirect after successful login
                response.sendRedirect("/home.html");
            } else {
                // Handle non-OIDC OAuth2 users
                System.out.println("Non-OIDC user: " + principal);
                response.sendRedirect("/home.html");
            }
        } else {
            response.sendRedirect("/login?error");
        }
    }
}

