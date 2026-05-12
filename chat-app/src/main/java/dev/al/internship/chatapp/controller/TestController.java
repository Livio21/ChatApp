package dev.al.internship.chatapp.controller;

import dev.al.internship.chatapp.model.entity.ChatRoom;
import dev.al.internship.chatapp.service.ChatRoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
class TestController {

    private final ChatRoomService chatRoomService;

    TestController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/test")
    public String test() {
        return "Success";
    }

    @GetMapping("/chat/{id}")
    public ChatRoom getChatRoom(@PathVariable long id ){
        return chatRoomService.getChatRoomById(id);
    }

    @GetMapping("/chat-rooms")
    public List<ChatRoom> getChatRoom( ){
        return chatRoomService.getAllChatRooms();
    }

    @PostMapping("/add-room")
    public String addRoom(@RequestBody ChatRoom chatRoom){
        chatRoomService.createChatRoom(chatRoom);
        return "Created room" + chatRoom.getName();
    }


}
