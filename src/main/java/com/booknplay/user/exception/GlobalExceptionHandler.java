package com.booknplay.user.exception;


import com.booknplay.user.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(

            HttpStatus status,

            String message,

            HttpServletRequest request) {

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()

                .timestamp(LocalDateTime.now())

                .status(status.value())

                .error(status.getReasonPhrase())

                .message(message)

                .path(request.getRequestURI())

                .build();

        return ResponseEntity.status(status).body(errorResponse);

    }

    @ExceptionHandler(UserNotFoundException.class)

    public ResponseEntity<ErrorResponseDto> handleUserNotFound(

            UserNotFoundException ex, HttpServletRequest request) {

        return buildErrorResponse(

                HttpStatus.NOT_FOUND, ex.getMessage(), request);

    }

    @ExceptionHandler(DuplicateEmailException.class)

    public ResponseEntity<ErrorResponseDto> handleDuplicateEmail(

            DuplicateEmailException ex, HttpServletRequest request) {

        return buildErrorResponse(

                HttpStatus.CONFLICT, ex.getMessage(), request);

    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(

            InvalidCredentialsException ex, HttpServletRequest request) {

        return buildErrorResponse(

                HttpStatus.UNAUTHORIZED, ex.getMessage(), request);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(

            Exception ex, HttpServletRequest request) {

        return buildErrorResponse(

                HttpStatus.INTERNAL_SERVER_ERROR,

                "An unexpected error occurred",

                request);

    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidResetToken(
            InvalidResetTokenException ex,
            HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

}
