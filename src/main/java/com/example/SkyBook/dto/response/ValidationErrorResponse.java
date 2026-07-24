package com.example.SkyBook.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(

        LocalDateTime timestamp,
        int status,
        String error,
        Map<String, String> errors

) {
}