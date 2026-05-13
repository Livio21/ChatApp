package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;

    public ChatMessageDto processIncoming(ChatMessageDto dto) {

        ChatMessage entity = ChatMessage.builder()
                .message(dto.getMessage())
                .sender(dto.getSender())
                .creationDate(LocalDateTime.now().toString())
                .messageType(dto.getMessageType())
                .build();

        repository.save(entity);

        return dto;
    }
}