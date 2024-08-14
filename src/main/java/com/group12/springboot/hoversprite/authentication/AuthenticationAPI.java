package com.group12.springboot.hoversprite.authentication;

import java.text.ParseException;

import com.group12.springboot.hoversprite.user.IdTokenRequest;
import com.nimbusds.jose.JOSEException;

public interface AuthenticationAPI {

    public String loginOAuthGoogle(IdTokenRequest requestBody);

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    public IntrospectTokenResponse introspect(IntrospectTokenRequest request) throws JOSEException, ParseException;
}
