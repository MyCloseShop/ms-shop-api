package com.etna.gpe.mycloseshop.ms_shop_api.dtos.error;


import java.util.Map;

public record ResponseError(Integer status, String message, Map<String, Object> errors) {
}
