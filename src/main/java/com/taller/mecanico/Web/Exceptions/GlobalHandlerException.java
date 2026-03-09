package com.taller.mecanico.Web.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.taller.mecanico.Domain.Exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalHandlerException {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(404).body(new Error(e.getMessage(), "Not Found"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(400).body(new Error(e.getMessage(), "Bad Request"));
    }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e){
        return ResponseEntity.status(500).body(new Error(e.getMessage(), "Internal Server Error"));
    }
}
