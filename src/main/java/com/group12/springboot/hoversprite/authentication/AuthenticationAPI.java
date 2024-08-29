package com.group12.springboot.hoversprite.authentication;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationAPI {

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response);

    public IntrospectTokenResponse introspect(IntrospectTokenRequest request) throws JOSEException, ParseException;
}
