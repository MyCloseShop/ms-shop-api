package com.etna.gpe.mycloseshop.ms_shop_api.exceptions;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalFeignExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ResponseError> handleFeignException(FeignException ex) {
        // Extraire les détails de la réponse Feign
        HttpStatus status = HttpStatus.resolve(ex.status());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", ex.contentUTF8());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(new ResponseError(
                status.value(),
                status.getReasonPhrase(),
                errorResponse
        ));
    }
}
