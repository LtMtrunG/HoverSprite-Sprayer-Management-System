package com.group12.springboot.hoversprite.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.group12.springboot.hoversprite.dataTransferObject.request.auth.AuthenticationRequest;
import com.group12.springboot.hoversprite.dataTransferObject.request.auth.IntrospectTokenRequest;
import com.group12.springboot.hoversprite.dataTransferObject.response.AuthenticationResponse;
import com.group12.springboot.hoversprite.dataTransferObject.response.IntrospectTokenResponse;
import com.group12.springboot.hoversprite.entity.Role;
import com.group12.springboot.hoversprite.entity.User;
import com.group12.springboot.hoversprite.exception.CustomException;
import com.group12.springboot.hoversprite.exception.ErrorCode;
import com.group12.springboot.hoversprite.repository.UserRepository;
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

import lombok.experimental.NonFinal;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @NonFinal
    protected static final String SIGNER_KEY = "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN";

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByPhoneNumber(request.getEmail());
        }

        User user = userOpt.orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_EXISTS));

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

    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                                                    .subject(user.getEmail())
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

    private String buildScope(User user) {
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