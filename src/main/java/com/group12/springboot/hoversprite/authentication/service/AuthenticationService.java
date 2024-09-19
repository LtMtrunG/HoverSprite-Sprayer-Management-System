package com.group12.springboot.hoversprite.authentication.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationAPI {
    private final UserAPI userAPI;

    @NonFinal
    protected static final String SIGNER_KEY = "WN1p+NNBEUYPdgLAec9Glzja6hTei7ElFAk975/CDLEIy6dmlrwofb4fdNRKuouN";

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response){
        UserAuthenticateDTO user = userAPI.findUserByEmail(request.getEmail());

        if (user == null) {
            System.out.println(processPhoneNumber(request.getEmail()));
            user = userAPI.findUserByPhoneNumber(processPhoneNumber(request.getEmail()));
            if (user == null) {
                throw new CustomException(ErrorCode.EMAIL_PHONE_NOT_EXISTS);
            }
            System.out.println("AUTH");
        }

        System.out.println("AUTH");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated){
            throw new CustomException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)        // HTTP-only flag
                .secure(false)          // Use secure flag if using HTTPS
                .path("/")             // Cookie available to the entire domain
                .maxAge(6 * 60 * 60) // Set cookie expiration (7 days here)
                .sameSite("Lax")    // CSRF protection
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setRole(user.getRole().getName());
        authenticationResponse.setAuthenticated(true);

        return authenticationResponse;
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

    private String processPhoneNumber(String phoneNumber) {
        // Remove all spaces from the phone number
        phoneNumber = phoneNumber.replaceAll("\\s+", "");

        // Replace the +84 with 0 if it starts with +84
        if (phoneNumber.startsWith("+84")) {
            phoneNumber = phoneNumber.replaceFirst("\\+84", "0");
        }

        return phoneNumber;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        Cookie cookie = new javax.servlet.http.Cookie("authToken", null);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/"); // Ensure the path matches the one used for setting the cookie
//        cookie.setMaxAge(0); // Set the cookie to expire immediately
//        response.addCookie(cookie);

        ResponseCookie cookie = ResponseCookie.from("jwt", null)
                .httpOnly(true)        // HTTP-only flag
                .secure(false)          // Use secure flag if using HTTPS
                .path("/")             // Cookie available to the entire domain
                .maxAge(0) // Set cookie expiration (7 days here)
                .sameSite("Lax")    // CSRF protection
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Invalidate Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

    }
}