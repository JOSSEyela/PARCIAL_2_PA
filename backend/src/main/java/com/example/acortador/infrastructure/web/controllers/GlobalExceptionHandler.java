package com.example.acortador.infrastructure.web.controllers;

import com.example.acortador.domain.exception.InvalidUrlException;
import com.example.acortador.domain.exception.ShortLinkNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, String>> invalidUrl(InvalidUrlException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> invalidBody(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "El enlace no puede estar vacío"));
    }

    @ExceptionHandler(ShortLinkNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ShortLinkNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
