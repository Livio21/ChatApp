package dev.al.internship.chatapp.controller;

import dev.al.internship.chatapp.model.dto.ChatMessageDto;
import dev.al.internship.chatapp.model.dto.ChatRoomDto;
import dev.al.internship.chatapp.model.dto.UserDto;
import dev.al.internship.chatapp.model.entity.ChatMessage;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.model.entity.User;
import dev.al.internship.chatapp.service.ChatRoomService;
import dev.al.internship.chatapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    public ChatRoomController(
            ChatRoomService chatRoomService,
            UserService userService
    ) {
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }

    @GetMapping("/chat-rooms/{roomId}")
    public ChatRoomDto getChatRoom(@PathVariable long roomId) {

        ChatRoom chatRoom = chatRoomService.getChatRoomById(roomId);
        Set<UserDto> users = chatRoomService.getUsers(roomId);
        Set<ChatMessageDto> messages =  chatRoomService.getMessages(roomId);

        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getDescription(),
                chatRoom.getOwnerId(),
                users,
                messages
        );
    }

//    @GetMapping("/chat-rooms/{roomId}/messages")
//    public Set<ChatMessageDto> getMessages(@PathVariable long roomId) {
//
//        return chatRoomService.getMessages(roomId)
//                .stream()
//                .map(msg -> new ChatMessageDto(
//                        msg.getId(),
//                        msg.getMessage(),
//                        msg.getSender(),
//                        msg.getCreationDate(),
//                        msg.getMessageType()
//                ))
//                .collect(Collectors.toSet());
//    }
//
//    @GetMapping("/chat-rooms/{roomId}/users")
//    public Set<UserDto> getUsers(@PathVariable long roomId) {
//
//        return chatRoomService.getUsers(roomId)
//                .stream()
//                .map(user -> new UserDto(
//                        user.getId(),
//                        user.getUsername(),
//                        user.getEmail()
//                ))
//                .collect(Collectors.toSet());
//    }

    @GetMapping("/chat-rooms")
    public List<ChatRoomDto> getChatRooms() {
        return chatRoomService.getAllChatRoomsDto();
    }

    @PostMapping("/add-room")
    public void addRoom(
            @RequestBody ChatRoom chatRoom,
            Authentication authentication
    ) {

        User user = userService.syncUser(authentication);
        Set<User> users = new HashSet<>();

        chatRoom.setOwnerId(String.valueOf(user.getId()));
        users.add(user);
        chatRoom.setRegisteredUsers(users);
        chatRoomService.createChatRoom(chatRoom);

    }

    @PostMapping("/chat-rooms/{roomId}/join")
    public void joinRoom(
            @PathVariable long roomId,
            Authentication authentication
    ) {

        User user = userService.syncUser(authentication);

        chatRoomService.joinRoom(roomId, user.getId());

    }
}