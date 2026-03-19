package com.taller.mecanico.Domain.Exceptions;

public class BadRequestException  extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
