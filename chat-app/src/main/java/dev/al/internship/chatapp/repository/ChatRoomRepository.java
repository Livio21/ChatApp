package dev.al.internship.chatapp.repository;

import dev.al.internship.chatapp.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findById(long id);

    @Query("""
    SELECT DISTINCT r
    FROM ChatRoom r
    JOIN r.registeredUsers u
    WHERE u.id = :userId
""")
    List<ChatRoom> findAllByMemberId(Long userId);
}