package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.model.entity.MessageType;
import dev.al.internship.chatapp.repository.ChatRoomRepository;
import dev.al.internship.chatapp.repository.MessageRepository;
import dev.al.internship.chatapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageDto processIncoming(
            long roomId,
            ChatMessageDto dto,
            String sender
    ) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

            ChatMessage message = ChatMessage.builder()
                    .message(dto.getMessage())
                    .sender(sender)
                    .creationDate(LocalDateTime.now().toString())
                    .messageType(dto.getMessageType())
                    .room(room)
                    .build();

        if(dto.getMessageType().equals(MessageType.CHAT_MESSAGE)){
            messageRepository.save(message);
        }
        return new ChatMessageDto(
                message.getId(),
                message.getMessage(),
                message.getSender(),
                message.getCreationDate(),
                message.getMessageType()
        );
    }
}