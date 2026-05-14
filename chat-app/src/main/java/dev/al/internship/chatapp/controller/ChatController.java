package dev.al.internship.chatapp.controller;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.service.ChatRoomService;
import dev.al.internship.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable long roomId,
            @Payload ChatMessageDto message,
            Principal principal
    ) {
        chatRoomService.assertMember(roomId, principal.getName());
        ChatMessageDto processed = messageService.processIncoming(message);

        messagingTemplate.convertAndSend(
                "/topic/messages/" + roomId,
                processed
        );
    }
}