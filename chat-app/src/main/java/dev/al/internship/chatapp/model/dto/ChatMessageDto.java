package dev.al.internship.chatapp.model.dto;

import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.MessageType;
import lombok.Value;

/**
 * DTO for {@link ChatMessage}
 */
@Value
public class ChatMessageDto {
    String message;
     String sender;
     String creationDate;
     MessageType messageType;
}