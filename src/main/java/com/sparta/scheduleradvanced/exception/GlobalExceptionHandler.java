package com.sparta.scheduleradvanced.exception;

import com.sparta.scheduleradvanced.dto.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append((fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage() + " / "));
        }

        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setMessage(errorMessage.toString());
        exceptionResponseDto.setStatusCode(HttpStatus.BAD_REQUEST.value()); // HttpStatus Code 400 으로 설정
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDto);
    }

    // IllegalArgumentException 예외처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setMessage(e.getMessage());
        exceptionResponseDto.setStatusCode(HttpStatus.FORBIDDEN.value()); // HttpStatus Code 403 으로 설정
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponseDto);
    }

    // TokenException 예외처리
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ExceptionResponseDto> handleTokenException(TokenException e) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setMessage(e.getMessage());
        exceptionResponseDto.setStatusCode(HttpStatus.BAD_REQUEST.value()); // HttpStatus Code 400 으로 설정
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDto);
    }
}

