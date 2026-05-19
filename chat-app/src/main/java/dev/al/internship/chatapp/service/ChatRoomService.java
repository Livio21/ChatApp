package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.model.dto.ChatRoomDto;
import dev.al.internship.chatapp.model.dto.UserDto;
import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.model.entity.User;
import dev.al.internship.chatapp.repository.ChatRoomRepository;
import dev.al.internship.chatapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
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

    public List<ChatRoomDto> getAllChatRoomsDtoWhereMember( Authentication authentication) {

          Optional<User> authUser = userRepository.findByUsername(authentication.getName());

//        List<ChatRoomDto> chatrooms =  chatRoomRepository.findAll()
//                .stream()
//                .map(room -> new ChatRoomDto(
//                        room.getId(),
//                        room.getName(),
//                        room.getDescription(),
//                        getOwner(room.getId()),
//                        getUsers(room.getId()),
//                        getMessages(room.getId())
//                ))
//                .toList();
//        return chatrooms;
        if (authUser.isPresent() ) {
        return chatRoomRepository.findAllByMemberUsername(authUser.get().getUsername())
                .stream()
                .map(room -> new ChatRoomDto(
                        room.getId(),
                        room.getName(),
                        room.getDescription(),
                        new UserDto(room.getOwner().getId(),room.getOwner().getUsername(),room.getOwner().getEmail()),
                        room.getRegisteredUsers()
                                .stream()
                                .map(user -> new UserDto(
                                        user.getId(),
                                        user.getUsername(),
                                        user.getEmail()
                                ))
                                .collect(Collectors.toSet()),

                        room.getChatMessages()
                                .stream()
                                .map(message -> new ChatMessageDto(
                                        message.getId(),
                                        message.getMessage(),
                                        message.getSender(),
                                        message.getCreationDate(),
                                        message.getMessageType()
                                ))
                                .collect(Collectors.toSet())
                ))
                .toList();
        }else {
            return null;
        }

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
        if(getOwner(roomId).getId() == userId){
            throw new RuntimeException("You are the owner of the room, you can't join this room");
        }

        room.getRegisteredUsers().add(user);
        chatRoomRepository.save(room);
    }

    public void removeMember(long roomId, long userId, Authentication authentication) {
        ChatRoom room = getChatRoomById(roomId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<User> authOwner = userRepository.findByUsername(authentication.getName());

        if(room.getOwner().getId() == userId){
            throw new RuntimeException("Cannot remove owner user "+room.getOwner().getId() + " "+  userId );
        }
        if (authOwner.isPresent() && room.getOwner().getId() != authOwner.get().getId()) {
            throw new RuntimeException("You cannot remove users as you are not the owner of this room" + room.getOwner().getId() + authOwner.get().getId());
        }else {
            room.getRegisteredUsers().remove(user);
            chatRoomRepository.save(room);
        }
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

    public UserDto getOwner(long roomId) {
        ChatRoom room = getChatRoomById(roomId);

        UserDto owner = new UserDto(
                room.getOwner().getId(),
                room.getOwner().getUsername(),
                null
        );

        return owner;
    }
}