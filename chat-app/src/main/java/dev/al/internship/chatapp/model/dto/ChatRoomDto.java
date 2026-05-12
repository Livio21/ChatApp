package dev.al.internship.chatapp.model.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dev.al.internship.chatapp.model.entity.ChatRoom}
 */
@Value
public class ChatRoomDto implements Serializable {
    Long id;
    String name;
    String description;
    Long ownerId;
}