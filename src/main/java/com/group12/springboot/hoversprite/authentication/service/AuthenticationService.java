package com.group12.springboot.hoversprite.authentication.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.StringJoiner;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.group12.springboot.hoversprite.user.IdTokenRequest;
import com.group12.springboot.hoversprite.user.UserOAuth2DTO;
import com.group12.springboot.hoversprite.user.entity.User;
import com.group12.springboot.hoversprite.user.enums.RoleType;
import com.group12.springboot.hoversprite.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.group12.springboot.hoversprite.authentication.AuthenticationAPI;
import com.group12.springboot.hoversprite.authentication.AuthenticationRequest;
import com.group12.springboot.hoversprite.authentication.AuthenticationResponse;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenRequest;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenResponse;
import com.group12.springboot.hoversprite.common.Role;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.user.UserAPI;
import com.group12.springboot.hoversprite.user.UserAuthenticateDTO;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
public class AuthenticationService implements AuthenticationAPI {
    private final UserAPI userAPI;

    private final GoogleIdTokenVerifier verifier;

    @NonFinal
    protected static final String SIGNER_KEY = "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN";


    public AuthenticationService(@Value("${app.googleClientId}") String clientId, UserAPI userAPI) {
        this.userAPI = userAPI;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        UserAuthenticateDTO user = userAPI.findUserByEmail(request.getEmail());

        if (user == null) {
            user = userAPI.findUserByPhoneNumber(request.getEmail());
            if (user == null) {
                throw new CustomException(ErrorCode.EMAIL_NOT_EXISTS);
            }
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated){
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(token);
        authenticationResponse.setAuthenticated(true);

        return authenticationResponse;
    }

    @Override
    public String loginOAuthGoogle(IdTokenRequest requestBody) {
        UserOAuth2DTO user = verifyIDToken(requestBody.getIdToken());
        if (user == null) {
            throw new IllegalArgumentException();
        }
        user = userAPI.createOrUpdateUser(user);
        UserAuthenticateDTO userAuthenticateDTO = userAPI.findUserByEmail(user.getEmail());

        if (userAuthenticateDTO == null) {
            throw new CustomException(ErrorCode.EMAIL_NOT_EXISTS);
        }
        return generateToken(userAuthenticateDTO);
    }

    private UserOAuth2DTO verifyIDToken(String idToken) {
        try {
            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                return null;
            }
            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            String fullName = (String) payload.get("name");
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");

            UserOAuth2DTO userOAuth2DTO = new UserOAuth2DTO();
            userOAuth2DTO.setEmail(email);
            userOAuth2DTO.setFullName(fullName);
            return userOAuth2DTO;
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }

    private String generateToken(UserAuthenticateDTO user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                                                    .subject(user.getId().toString())
                                                    .issuer("hoversprite.com")
                                                    .issueTime(new Date())
                                                    .expirationTime(new Date(
                                                            Instant.now().plus(6, ChronoUnit.HOURS).toEpochMilli()
                                                    ))
                                                    .claim("scope", buildScope(user))
                                                    .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IntrospectTokenResponse introspect(IntrospectTokenRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var valid = signedJWT.verify(verifier);
        IntrospectTokenResponse introspectTokenResponse = new IntrospectTokenResponse();
        introspectTokenResponse.setValid(valid && expirationTime.after(new Date()));
        return introspectTokenResponse;
    }

    private String buildScope(UserAuthenticateDTO user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Role role = user.getRole();
        if (role != null) {
            stringJoiner.add("ROLE_" + role.getName());
            if (!CollectionUtils.isEmpty(role.getPermissions()))
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
        }

        return stringJoiner.toString();
    }
}