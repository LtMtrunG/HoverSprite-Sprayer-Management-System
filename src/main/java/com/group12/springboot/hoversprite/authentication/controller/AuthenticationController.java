package com.group12.springboot.hoversprite.authentication.controller;

import java.text.ParseException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.group12.springboot.hoversprite.user.IdTokenRequest;
import com.group12.springboot.hoversprite.user.UserAPI;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group12.springboot.hoversprite.authentication.AuthenticationRequest;
import com.group12.springboot.hoversprite.authentication.AuthenticationResponse;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenRequest;
import com.group12.springboot.hoversprite.authentication.IntrospectTokenResponse;
import com.group12.springboot.hoversprite.authentication.service.AuthenticationService;
import com.group12.springboot.hoversprite.common.ApiResponse;
import com.nimbusds.jose.JOSEException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    private static final String CLIENT_ID = "407408718192.apps.googleusercontent.com";
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        ApiResponse<AuthenticationResponse> apiResponse = new ApiResponse<>();
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        apiResponse.setResult(authenticationResponse);
        return apiResponse;
    }

    public ResponseEntity<String> verifyToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))  // Your client ID here
                .build();

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
            System.out.println(idToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token verification error.");
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Handle the payload as needed
            return ResponseEntity.ok("User logged in successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
        }
    }

    @PostMapping("/login/gmail")
    ResponseEntity LoginWithGoogleOauth2(@RequestBody IdTokenRequest requestBody, HttpServletResponse response) {
        String authToken = authenticationService.loginOAuthGoogle(requestBody);
        final ResponseCookie cookie = ResponseCookie.from("AUTH-TOKEN", authToken)
                .httpOnly(true)
                .maxAge(7 * 24 * 3600)
                .path("/")
                .secure(false)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/login/gmail")
//    public ResponseEntity<String> loginWithGoogleOauth2(@RequestBody IdTokenRequest requestBody) {
//        String idToken = requestBody.getIdToken();  // Ensure this is correctly populated
//        return verifyToken(idToken);
//    }

//    @PostMapping("/login/gmail")
//    public ResponseEntity<String> loginWithGoogle(@RequestBody IdTokenRequest requestBody) {
//        String idTokenString = requestBody.getIdToken();
//
//        System.out.println(idTokenString);
//        // Verify the idToken
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
//                .setAudience(Collections.singletonList(CLIENT_ID))
//                .build();
//
//        GoogleIdToken idToken = null;
//        try {
//            idToken = verifier.verify(idTokenString);
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Invalid ID token1");
//        }
//
//        if (idToken != null) {
//            GoogleIdToken.Payload payload = idToken.getPayload();
//
//            // Get user information from the token payload
//            String userId = payload.getSubject();  // Use this to uniquely identify the user
//            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");
//
//            // You can now authenticate the user in your system and create a session
//            // Return a token, session ID, or success message as needed
//
//            return ResponseEntity.ok("User logged in successfully");
//        } else {
//            return ResponseEntity.status(401).body("Invalid ID token");
//        }
//    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectTokenResponse> introspect(@RequestBody IntrospectTokenRequest request) throws ParseException, JOSEException {
        ApiResponse<IntrospectTokenResponse> apiResponse = new ApiResponse<>();
        IntrospectTokenResponse introspectTokenResponse = authenticationService.introspect(request);
        apiResponse.setResult(introspectTokenResponse);
        return apiResponse;
    }
}