package dev.al.internship.chatapp.repository;

import dev.al.internship.chatapp.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}