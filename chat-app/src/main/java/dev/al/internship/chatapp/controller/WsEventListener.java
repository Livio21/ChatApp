package dev.al.internship.chatapp.controller;

import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@RequiredArgsConstructor
public class WsEventListener {
    
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWsDisconnectListener( SessionDisconnectEvent e ){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(e.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username == null) {
            return;
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .messageType(MessageType.LEAVE)
                .sender(username)
                .build();

        messagingTemplate.convertAndSend("/topic/messages", chatMessage);
        
    }

//    @EventListener
//    public void handleUserActiveInChat(SessionSubscribeEvent e ){
//
//    }
//
//    @EventListener
//    public void handleUserInactiveInChat(SessionUnsubscribeEvent e ){
//
//    }
}
