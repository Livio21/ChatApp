package dev.al.internship.chatapp.model.dto;

import lombok.Value;

@Value
public class ApiErrorDto {
    int status;
    String message;
    String path;
}
