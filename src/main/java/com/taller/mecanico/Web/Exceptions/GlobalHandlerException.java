package com.taller.mecanico.Web.Exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.taller.mecanico.Domain.Exceptions.AlreadyExistsException;
import com.taller.mecanico.Domain.Exceptions.BadRequestException;
import com.taller.mecanico.Domain.Exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalHandlerException {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> handleNotFoundException(NotFoundException e){
        return ResponseEntity.status(404).body(new Error(e.getMessage(), "Not Found"));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Error> handleAlreadyExistsException(AlreadyExistsException e){
        return ResponseEntity.status(409).body(new Error(e.getMessage(), "Already Exists"));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> handleBadRequestException(BadRequestException e){
        return ResponseEntity.status(400).body(new Error(e.getMessage(), "Bad Request"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(400).body(new Error(e.getMessage(), "Bad Request"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Error> handleNoResourceFoundException(NoResourceFoundException e){
        return ResponseEntity.status(404).body(new Error(e.getMessage(), "Not Found"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        return ResponseEntity.status(400).body(new Error(e.getMessage(), "Bad Request"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Error> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Error error = new Error("Data Integrity Violation", ex.toString());
        return ResponseEntity.status(409).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        return ResponseEntity.status(400).body(new Error(e.getMessage(), "Bad Request"));

    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Error> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return ResponseEntity.status(400).body(new Error(e.getMessage(), "Bad Request"));
    }

     @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleException(Exception e){
        return ResponseEntity.status(500).body(new Error(e.getMessage(),e.getClass().getTypeName()));
    }
}
