package dev.al.internship.chatapp.repository;

import dev.al.internship.chatapp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}