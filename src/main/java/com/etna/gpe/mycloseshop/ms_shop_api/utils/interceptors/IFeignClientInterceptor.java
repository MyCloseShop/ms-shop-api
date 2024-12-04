package com.etna.gpe.mycloseshop.ms_shop_api.utils.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public interface IFeignClientInterceptor extends RequestInterceptor {
    @Override
    void apply(RequestTemplate template);
}
