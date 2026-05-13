package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.dto.UserDto;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.model.entity.User;
import dev.al.internship.chatapp.repository.ChatRoomRepository;
import dev.al.internship.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public ChatRoom getChatRoomById(long id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public void addMember(Long roomId, String username) {
        ChatRoom room = getChatRoomById(roomId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        room.getRegisteredUsers().add(user);
        chatRoomRepository.save(room);
    }

    public boolean isMember(Long roomId, String username) {
        ChatRoom room = getChatRoomById(roomId);

        return room.getRegisteredUsers()
                .stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    public void assertMember(Long roomId, String username) {
        if (!isMember(roomId, username)) {
            throw new SecurityException("Not a room member");
        }
    }
}