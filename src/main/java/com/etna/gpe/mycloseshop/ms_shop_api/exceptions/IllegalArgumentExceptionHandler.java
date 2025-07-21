package com.etna.gpe.mycloseshop.ms_shop_api.exceptions;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class IllegalArgumentExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid argument",
                Map.of("message", e.getMessage())
        ));
    }
}
