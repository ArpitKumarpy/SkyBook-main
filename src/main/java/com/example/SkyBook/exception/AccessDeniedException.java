package com.example.SkyBook.exception;

public class AccessDeniedException extends RuntimeException{
    
    public AccessDeniedException(String message){
        super(message);
    }
    
}
