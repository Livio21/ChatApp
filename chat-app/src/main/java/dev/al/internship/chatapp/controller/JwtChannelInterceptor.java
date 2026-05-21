package dev.al.internship.chatapp.controller;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public JwtChannelInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        System.out.println("STOMP COMMAND = " + accessor.getCommand());
        System.out.println("HEADERS = " + accessor.toNativeHeaderMap());
        if (StompCommand.CONNECT.equals(accessor.getCommand())
                || StompCommand.SEND.equals(accessor.getCommand())
                || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            String token = null;

            List<String> authHeader = accessor.getNativeHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                token = authHeader.get(0);
            }


            if (token == null) {
                List<String> alt = accessor.getNativeHeader("authorization");
                if (alt != null && !alt.isEmpty()) {
                    token = alt.get(0);
                }
            }

            if (token == null || token.isBlank()) {
                return message;
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Jwt jwt = jwtDecoder.decode(token);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            jwt.getClaimAsString("username"),
                            null,
                            List.of()
                    );

            accessor.setUser(authentication);
        }

        return message;
    }
}