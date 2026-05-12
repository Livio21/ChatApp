package dev.al.internship.chatapp.model.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dev.al.internship.chatapp.model.entity.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    String username;
    String email;
    String role;
}