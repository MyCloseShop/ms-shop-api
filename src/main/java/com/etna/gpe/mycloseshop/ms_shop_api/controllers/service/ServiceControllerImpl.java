package com.etna.gpe.mycloseshop.ms_shop_api.controllers.service;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreateServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreatedServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.ServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.services.service.IServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceControllerImpl implements IServiceController {
    private final IServiceService serviceService;

    public ServiceControllerImpl(IServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @Override
    public ResponseEntity<CreatedServiceDto> createServices(CreateServiceDto serviceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(serviceDto));
    }

    @Override
    public ResponseEntity<List<ServiceDto>> getServicesOfShop(String shopId) {
        return ResponseEntity.ok(serviceService.getServicesOfShop(shopId));
    }

    @Override
    public ResponseEntity<ServiceDto> getServiceById(String serviceId) {
        return ResponseEntity.ok(serviceService.getServiceById(serviceId));
    }
}
