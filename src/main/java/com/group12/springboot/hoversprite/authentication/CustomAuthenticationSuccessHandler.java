package com.group12.springboot.hoversprite.authentication;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.ServletException;
import lombok.experimental.NonFinal;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @NonFinal
    protected static final String SIGNER_KEY = "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN";

    public CustomAuthenticationSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // Cast the Authentication object to OAuth2AuthenticationToken
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();

            // Use OAuth2AuthorizedClientService to get the OAuth2AuthorizedClient
            String clientRegistrationId = oauth2Token.getAuthorizedClientRegistrationId();
            OAuth2AuthorizedClient authorizedClient = authorizedClientService
                    .loadAuthorizedClient(clientRegistrationId, oauth2User.getName());

            if (authorizedClient != null) {

                OAuth2User oauthUser = oauth2Token.getPrincipal();

                // Now you can access the attributes
                Map<String, Object> userAttributes = oauthUser.getAttributes();

                // Extract name and email
                String name = (String) userAttributes.get("name");
                String email = (String) userAttributes.get("email");

                System.out.println("Name: " + name);
                System.out.println("Email: " + email);

                String token = generateToken(email, name);

                response.sendRedirect("/hoversprite/login.html?token=" + token);
            } else {
                response.sendRedirect("/login?error");
            }
        } else {
            response.sendRedirect("/login?error");
        }
    }

    private String generateToken(String email, String name) {
        // Combine email and name to create the subject
        String subject = email + ":" + name;

        // Create JWT Claims Set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("hoversprite.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(6, ChronoUnit.HOURS).toEpochMilli()))
                .claim("email", email) // Optionally store the email and name as claims
                .claim("name", name)
                .build();

        // Create JWSObject with the header and payload
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Sign the JWSObject and return the serialized token
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
