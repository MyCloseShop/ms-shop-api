package com.etna.gpe.mycloseshop.ms_shop_api.services.service;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreateServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreatedServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.ServiceDto;

import java.util.List;

public interface IServiceService {
    CreatedServiceDto createService(CreateServiceDto serviceDto);

    List<ServiceDto> getServicesOfShop(String shopId);

    ServiceDto getServiceById(String serviceId);
}
