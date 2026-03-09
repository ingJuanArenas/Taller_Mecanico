package com.taller.mecanico.Web.Exceptions;

public record Error(
    String message,
    String type
) {}