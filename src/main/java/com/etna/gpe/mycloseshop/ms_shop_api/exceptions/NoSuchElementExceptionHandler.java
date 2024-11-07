package com.etna.gpe.mycloseshop.ms_shop_api.exceptions;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class NoSuchElementExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseError> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseError(
                    HttpStatus.NOT_FOUND.value(),
                    "No such element",
                    Map.of(
                            "message", e.getMessage()
                    )
                )
        );
    }
}
