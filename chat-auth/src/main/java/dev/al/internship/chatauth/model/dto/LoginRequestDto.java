package dev.al.internship.chatauth.model.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dev.al.internship.chatauth.model.entity.User}
 */
@Value
public class LoginRequestDto implements Serializable {
    String email;
    String password;
}