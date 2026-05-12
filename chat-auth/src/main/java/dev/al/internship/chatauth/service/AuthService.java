package dev.al.internship.chatauth.service;

import dev.al.internship.chatauth.model.dto.LoginRequestDto;
import dev.al.internship.chatauth.model.dto.RegisterRequestDto;
import dev.al.internship.chatauth.model.entity.JwtResponse;
import dev.al.internship.chatauth.model.entity.User;
import dev.al.internship.chatauth.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository repository, PasswordEncoder encoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public JwtResponse registerUser(RegisterRequestDto registerReq) throws UsernameNotFoundException, BadCredentialsException, DataIntegrityViolationException {

        if(registerReq.getUsername().isEmpty() || registerReq.getEmail().isEmpty() || registerReq.getPassword().isEmpty()) {
            throw new BadCredentialsException("Missing credentials");
        }

        if (repository.findByUsername(registerReq.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerReq.getUsername());
        user.setEmail(registerReq.getEmail());
        user.setPassword(encoder.encode(registerReq.getPassword()));
        repository.save(user);
        String token = jwtService.generateToken(user);

        JwtResponse jwtResponse = new JwtResponse(token, jwtService.getUsername(token), jwtService.getExpiration(token));

        return jwtResponse;
    }

    public JwtResponse loginUser(LoginRequestDto loginReq) throws UsernameNotFoundException, BadCredentialsException {

        User user = repository.findByEmail(loginReq.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Email not found"));

//        if (encoder.matches(loginReq.getPassword(), user.getPassword())) {
//            return jwtService.generateToken(user);
//        }
//
//        boolean validPassword = encoder.matches(loginReq.getPassword(), user.getPassword());
//        if (!validPassword) {
//            throw new BadCredentialsException("Password is not valid");
//        }
        if(loginReq.getEmail() == null || loginReq.getPassword() == null) {
            throw new BadCredentialsException("Missing credentials");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReq.getEmail(),
                        loginReq.getPassword()
                )
        );

        String token = jwtService.generateToken(user);

        JwtResponse jwtResponse = new JwtResponse(token, jwtService.getUsername(token), jwtService.getExpiration(token));
        return jwtResponse;

    }

}
