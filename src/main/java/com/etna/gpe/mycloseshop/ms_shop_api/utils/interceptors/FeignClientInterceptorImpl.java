package com.etna.gpe.mycloseshop.ms_shop_api.utils.interceptors;

import com.etna.gpe.mycloseshop.security_api.config.JwtTokenUtil;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FeignClientInterceptorImpl implements IFeignClientInterceptor {
    public static final String MS_SHOP_API = "ms-shop-api";
    // init logger
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignClientInterceptorImpl.class);
    private final JwtTokenUtil jwtTokenUtil;

    public FeignClientInterceptorImpl(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void apply(RequestTemplate template) {
        LOGGER.info("Intercepting request to add JWT token");

        UUID requestUUID = UUID.randomUUID();
        List<String> roles = List.of("ROLE_ADMIN");

        String token = jwtTokenUtil.generateTokenForMsWith(MS_SHOP_API, requestUUID, roles);

        LOGGER.debug("Token generated: {}", token);

        template.header("Authorization", "Bearer " + token);

        LOGGER.info("Request UUID: {}", requestUUID);
        LOGGER.info("Token generated.");
        LOGGER.debug("The request is: {}", template);
    }
}
