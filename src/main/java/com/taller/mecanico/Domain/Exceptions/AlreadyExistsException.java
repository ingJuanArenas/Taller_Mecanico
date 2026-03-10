package com.taller.mecanico.Domain.Exceptions;

public class AlreadyExistsException extends RuntimeException {
    AlreadyExistsException(String message) {
        super(message);
    }
}
