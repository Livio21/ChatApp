package dev.al.internship.chatauth.model.dto;

import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dev.al.internship.chatauth.model.entity.User}
 */
@Value
public class RegisterRequestDto implements Serializable {
    @NonNull String username;
    @NonNull String email;
    @NonNull String password;
}