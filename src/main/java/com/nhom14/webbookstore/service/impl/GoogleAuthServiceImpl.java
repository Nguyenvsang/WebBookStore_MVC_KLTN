package com.nhom14.webbookstore.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nhom14.webbookstore.service.GoogleAuthService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private static final String CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");

    @Override
    public GoogleIdToken.Payload authenticate(String idTokenString) {
        GoogleIdToken.Payload payload = null;
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                payload = idToken.getPayload();
            }
        } catch (GeneralSecurityException | IOException e) {
            // handle exception
        }
        return payload;
    }
}
