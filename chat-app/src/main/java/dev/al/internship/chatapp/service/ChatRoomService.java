package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoom getChatRoomById(long id){
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElse(null);
        return chatRoom;
    }

    public List<ChatRoom> getAllChatRooms(){
        return chatRoomRepository.findAll();
    }

    public void createChatRoom(ChatRoom chatRoom){
        chatRoomRepository.save(chatRoom);
    }

}
