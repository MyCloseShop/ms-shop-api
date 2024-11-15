package com.etna.gpe.mycloseshop.ms_shop_api.services.service;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreateServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreatedServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.ServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Service;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.IServiceMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IServiceRepository;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements IServiceService {

    private final IServiceRepository serviceRepository;
    private final IServiceMapper serviceMapper;
    private final IShopRepository shopRepository;

    public ServiceServiceImpl(
            IServiceRepository serviceRepository,
            IServiceMapper serviceMapper,
            IShopRepository shopRepository
                              ) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.shopRepository = shopRepository;
    }

    @Override
    public CreatedServiceDto createService(CreateServiceDto serviceDto) {
        Shop shop = shopRepository.findById(serviceDto.shopId()).orElseThrow();

        Service service = Service.builder()
                .name(serviceDto.name())
                .description(serviceDto.description())
                .price(serviceDto.price())
                .duration(serviceDto.duration())
                .shop(shop)
                .build();
        return serviceMapper.toCreatedDto(serviceRepository.save(service));
    }

    @Override
    public List<ServiceDto> getServicesOfShop(String shopId) {
        return serviceRepository.findAllByShopId(UUID.fromString(shopId))
                .stream()
                .map(serviceMapper::toDto)
                .toList();
    }

    @Override
    public ServiceDto getServiceById(String serviceId) {
        return serviceRepository.findById(Integer.parseInt(serviceId))
                .map(serviceMapper::toDto)
                .orElseThrow();
    }
}
