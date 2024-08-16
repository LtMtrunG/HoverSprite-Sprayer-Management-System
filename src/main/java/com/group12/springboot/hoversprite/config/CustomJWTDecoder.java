package com.group12.springboot.hoversprite.config;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.group12.springboot.hoversprite.authentication.AuthenticationAPI;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenRequest;
import com.nimbusds.jose.JOSEException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomJWTDecoder implements JwtDecoder {
    protected static final String SIGNER_KEY = "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN";

    private final AuthenticationAPI authenticationAPI;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {
        IntrospectTokenRequest introspectTokenRequest = new IntrospectTokenRequest();
        introspectTokenRequest.setToken(token);

        try{
            var response = authenticationAPI.introspect(introspectTokenRequest);
            if (!response.isValid()) {
                System.out.println("Token validation failed");
                throw new JwtException("Token invalid");
            }
        }catch (JOSEException | ParseException e){
            throw new JwtException(e.getMessage());
            // throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}