package dev.al.internship.chatauth.controller;

import dev.al.internship.chatauth.model.dto.LoginRequestDto;
import dev.al.internship.chatauth.model.dto.RegisterRequestDto;
import dev.al.internship.chatauth.model.entity.JwtResponse;
import dev.al.internship.chatauth.service.AuthService;
import dev.al.internship.chatauth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {

    private final AuthService authService;

    public AuthController( AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/sign-up")
    public JwtResponse signUp(@RequestBody RegisterRequestDto registerReq) {
        return authService.registerUser(registerReq);
    }
    @PostMapping("/auth/sign-in")
    public JwtResponse signIn(@RequestBody LoginRequestDto loginReq) {
        return authService.loginUser(loginReq);
    }

    @GetMapping("/test-auth")
    public String testAuth() {
        return "Success";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String testException(UsernameNotFoundException e) {
        return "Username not found" + e.getMessage();
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String testException(BadCredentialsException e) {
        return "Bad credentials " + e.getMessage();
    }

}
