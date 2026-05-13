package dev.al.internship.chatapp.repository;

import dev.al.internship.chatapp.model.dto.UserDto;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findById(long id);


}