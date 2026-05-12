package dev.al.internship.chatauth.model.dto;


import lombok.Value;

@Value
public class AuthResponse {
    String accessToken;
    String expiresIn;
}
