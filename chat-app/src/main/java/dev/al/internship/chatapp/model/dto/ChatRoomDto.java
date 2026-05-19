package dev.al.internship.chatapp.model.dto;

import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link dev.al.internship.chatapp.model.entity.ChatRoom}
 */
@Value
public class ChatRoomDto{
    Long id;
    String name;
    String description;
    UserDto owner;
    Set<UserDto> users;
    Set<ChatMessageDto> chatMessages;
}