package dev.al.internship.chatapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
class TestController {



    @GetMapping("/test")
    public String test() {
        return "Success";
    }

}
