package dev.al.internship.chatapp.model.dto;

import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link dev.al.internship.chatapp.model.entity.ChatRoom}
 */
@Value
public class ChatRoomDto{
    Long id;
    String name;
    String description;
    UserDto owner;
    List<UserDto> members;
}