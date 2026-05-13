package dev.al.internship.chatapp.repository;

import dev.al.internship.chatapp.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

}