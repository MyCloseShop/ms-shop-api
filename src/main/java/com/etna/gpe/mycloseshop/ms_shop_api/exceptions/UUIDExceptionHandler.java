package com.etna.gpe.mycloseshop.ms_shop_api.exceptions;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class UUIDExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException e) {
        ResponseError responseError = new ResponseError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad request",
                Map.of("UUID", List.of(e.getMessage()))
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }
}
