package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.repository.ChatRoomRepository;
import dev.al.internship.chatapp.repository.MessageRepository;
import dev.al.internship.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;

    public ChatMessageDto processIncoming(ChatMessageDto chatMessageDto) {

        ChatMessage message = ChatMessage.builder()
                .message(chatMessageDto.getMessage())
                .sender(chatMessageDto.getSender())
                .creationDate(LocalDateTime.now().toString())
                .messageType(chatMessageDto.getMessageType())
                .build();

        repository.save(message);

        return chatMessageDto;
    }
}