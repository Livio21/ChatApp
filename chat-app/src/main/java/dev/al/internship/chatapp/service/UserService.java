package dev.al.internship.chatapp.service;

import dev.al.internship.chatapp.model.entity.User;
import dev.al.internship.chatapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User syncUser(Authentication authentication) {

        JwtAuthenticationToken jwt =
                (JwtAuthenticationToken) authentication;

        Long userId =
                Long.parseLong(jwt.getToken().getSubject());

        String username =
                jwt.getToken().getClaim("username");

        String email =
                jwt.getToken().getClaim("email");


        return userRepository.findById(userId)
                .map(existing -> {

                    existing.setUsername(username);
                    existing.setEmail(email);

                    return userRepository.save(existing);
                })
                .orElseGet(() -> {

                    User user = new User();

                    user.setId(userId);
                    user.setUsername(username);
                    user.setEmail(email);

                    return userRepository.save(user);
                });
    }
}