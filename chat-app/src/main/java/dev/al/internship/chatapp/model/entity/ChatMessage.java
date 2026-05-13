package dev.al.internship.chatapp.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String sender;
    private String creationDate;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom room;
}