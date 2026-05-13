package dev.al.internship.chatapp.controller;

import dev.al.internship.chatapp.model.dto.UserDto;
import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.model.entity.User;
import dev.al.internship.chatapp.service.ChatRoomService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
class ChatRoomController {

    private final ChatRoomService chatRoomService;

    ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/test")
    public String test() {
        return "Success";
    }

    @GetMapping("/chat-rooms/{id}")
    public ChatRoom getChatRoom(@PathVariable long id) {
        return chatRoomService.getChatRoomById(id);
    }

    @GetMapping("/chat-rooms")
    public List<ChatRoom> getChatRooms() {
        return chatRoomService.getAllChatRooms();
    }

    @PostMapping("/add-room")
    public String addRoom(@RequestBody ChatRoom chatRoom, Authentication authentication) {

        String username = authentication.getName();
        chatRoom.setOwnerId(username);
        chatRoomService.createChatRoom(chatRoom);
        return "Created room " + chatRoom.getName();
    }

    @PostMapping("/add-member/{id}")
    public String addMember(@PathVariable long id, @RequestParam String username) {
        chatRoomService.addMember(id, username);
        return "Added " + username + " to room " + id;
    }


}
