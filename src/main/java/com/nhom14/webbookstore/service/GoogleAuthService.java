package com.nhom14.webbookstore.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

public interface GoogleAuthService {
    GoogleIdToken.Payload authenticate(String idTokenString);
}
