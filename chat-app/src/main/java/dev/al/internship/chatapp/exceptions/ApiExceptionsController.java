package dev.al.internship.chatapp.exceptions;

import dev.al.internship.chatapp.exceptions.type.TestException;
import dev.al.internship.chatapp.model.dto.ApiErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionsController {

    @ExceptionHandler(TestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorDto> testExceptionHandler(TestException e,  HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorDto(
                        501,
                        e.getMessage(),
                        request.getRequestURI()
                ));
    }
}
