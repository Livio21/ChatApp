package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.model.dto.ChatRoomDto;
import dev.al.internship.chatapp.model.dto.UserDto;
import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.model.entity.User;
import dev.al.internship.chatapp.repository.ChatRoomRepository;
import dev.al.internship.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<ChatRoomDto> getAllChatRoomsDto() {
        return chatRoomRepository.findAll()
                .stream()
                .map(room -> new ChatRoomDto(
                        room.getId(),
                        room.getName(),
                        room.getDescription(),
                        room.getOwnerId(),
                        getUsers(room.getId()),
                        getMessages(room.getId())
                ))
                .collect(Collectors.toList());
    }

    public ChatRoom createChatRoom(ChatRoom chatRoom) {

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public void addMember(long roomId, long userId) {
        ChatRoom room = getChatRoomById(roomId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        room.getRegisteredUsers().add(user);
        chatRoomRepository.save(room);
    }

    public void joinRoom(long roomId, long userId) {
        ChatRoom room = getChatRoomById(roomId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(isMember(roomId, user.getUsername())){
            throw new RuntimeException("User is already member");
        }
        room.getRegisteredUsers().add(user);
        chatRoomRepository.save(room);
    }

    public boolean isMember(long roomId, String username) {
        ChatRoom room = getChatRoomById(roomId);

        return room.getRegisteredUsers()
                .stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }

    public void assertMember(long roomId, String username) {
        if (!isMember(roomId, username)) {
            throw new SecurityException("Not a room member");
        }
    }

    public Set<UserDto> getUsers(long roomId) {

        ChatRoom room = getChatRoomById(roomId);

        Set<User> users = room.getRegisteredUsers();

        Set<UserDto> usersDto =  users.stream().map(u ->
                new UserDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail()
        )
        ).collect(Collectors.toSet());


        return usersDto;
    }

    public Set<ChatMessageDto> getMessages(long roomId) {
        ChatRoom room = getChatRoomById(roomId);

        Set<ChatMessage> chatMessages = room.getChatMessages();

        Set<ChatMessageDto> chatMessageDtos = chatMessages.stream().map(c ->
                new ChatMessageDto(
                        c.getId(),
                        c.getMessage(),
                        c.getSender(),
                        c.getCreationDate(),
                        c.getMessageType()
                )).collect(Collectors.toSet());

        return chatMessageDtos;
    }
}