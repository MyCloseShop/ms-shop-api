package com.etna.gpe.mycloseshop.ms_shop_api.services.shop;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.location.LocationDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours.OpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.ShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.UpdateStripeAccountDto;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Location;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.OpeningHours;
import com.etna.gpe.mycloseshop.ms_shop_api.entity.Shop;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.ILocationMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.mappers.IOpeningHoursMapper;
import com.etna.gpe.mycloseshop.ms_shop_api.repository.IShopRepository;
import com.etna.gpe.mycloseshop.security_api.entity.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ShopService implements IShopService {
    public static final String UPDATED_STRIPE_ACCOUNT_ID_FOR_SHOP = "Updated Stripe account ID for shop: ";
    private final IShopRepository shopRepository;
    private final Logger logger = Logger.getLogger(ShopService.class.getName());
    private final IOpeningHoursMapper openingHoursMapper;
    private final ILocationMapper locationMapper;

    public ShopService(
            IShopRepository shopRepository,
            IOpeningHoursMapper openingHoursMapper,
            ILocationMapper locationMapper
    ) {
        this.shopRepository = shopRepository;
        this.openingHoursMapper = openingHoursMapper;
        this.locationMapper = locationMapper;
    }


    @Override
    public CreatedShopDto createShop(CreateShopWithLocationAndOpeningHoursDto shopDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();

        logger.info("Principal id : " + jwtUserDetails.getUserId());

        // log the username
        logger.info("User " + authentication.getName() + " is creating a shop");

        Location location = new Location();
        location.setAddress(shopDto.getLocation().getAddress());
        location.setCity(shopDto.getLocation().getCity());
        location.setPostalCode(shopDto.getLocation().getPostalCode());
        location.setOptionalInformation(shopDto.getLocation().getOptionalInformation());

        List<OpeningHours> openingHours = shopDto.getOpeningHours().stream().map(openingHoursDto -> {
            OpeningHours openingHours1 = new OpeningHours();
            openingHours1.setDayOfWeek(openingHoursDto.getDay());
            openingHours1.setStartTime(LocalTime.parse(openingHoursDto.getOpening()));
            openingHours1.setEndTime(LocalTime.parse(openingHoursDto.getClosing()));
            return openingHours1;
        }).toList();

        Shop shop = new Shop();
        shop.setName(shopDto.getName());
        shop.setDescription(shopDto.getDescription());
        shop.setLocation(location);
        shop.setOpeningHours(openingHours);
        shop.setUserId(shopDto.getUserId());

        // link location to shop
        location.setShop(shop);

        // link opening hours to shop
        openingHours.forEach(oh -> oh.setShop(shop));

        Shop savedShop = shopRepository.save(shop);

        CreatedShopDto createdShopDto = new CreatedShopDto();
        createdShopDto.setId(savedShop.getId());
        createdShopDto.setName(savedShop.getName());
        createdShopDto.setDescription(savedShop.getDescription());
        createdShopDto.setUserId(savedShop.getUserId());
        createdShopDto.setLocation(savedShop.getLocation().getId());
        createdShopDto.setOpeningHours(
                savedShop.getOpeningHours().stream().map(OpeningHours::getId).toList()
        );
        createdShopDto.setCreatedAt(savedShop.getCreatedAt().toString());
        createdShopDto.setUpdatedAt(savedShop.getUpdatedAt().toString());

        return createdShopDto;
    }

    @Override
    public List<ShopDto> getShops() {
        return shopRepository.findAll().stream().map(
                shop -> new ShopDto().toBuilder()
                    .shopId(shop.getId())
                    .name(shop.getName())
                    .description(shop.getDescription())
                    .ownerId(shop.getUserId())
                    .locationId(shop.getLocation().getId())
                    .openingHoursIds(
                            shop.getOpeningHours().stream().map(OpeningHours::getId).toList()
                    )
                    .build()
        ).toList();
    }

    @Override
    public ShopDto getShop(String shopId) {
        try {
            Shop shop = shopRepository.findById(UUID.fromString(shopId)).orElseThrow();
            return new ShopDto().toBuilder()
                    .shopId(shop.getId())
                    .name(shop.getName())
                    .description(shop.getDescription())
                    .ownerId(shop.getUserId())
                    .locationId(shop.getLocation().getId())
                    .openingHoursIds(
                            shop.getOpeningHours().stream().map(OpeningHours::getId).toList()
                    )
                    .build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid shop id");
        }
    }

    @Override
    public List<OpeningHoursDto> getShopOpeningHours(String shopId) {
        Shop shop = shopRepository.findById(UUID.fromString(shopId)).orElseThrow();
        return shop.getOpeningHours().stream().map(openingHoursMapper::toDto).toList();
    }

    @Override
    public LocationDto getShopLocation(String shopId) {
        Shop shop = shopRepository.findById(UUID.fromString(shopId)).orElseThrow();
        return locationMapper.toDto(shop.getLocation());
    }

    @Override
    public String getShopStripeAccountId(String shopId) {
        UUID uuid = UUID.fromString(shopId);
        Shop shop = shopRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));

        String stripeAccountId = shop.getStripeAccountId();
        if (stripeAccountId == null || stripeAccountId.isEmpty()) {
            throw new RuntimeException("No Stripe account configured for shop: " + shopId);
        }

        return stripeAccountId;
    }
    
    @Override
    public String updateShopStripeAccountId(String shopId, UpdateStripeAccountDto stripeAccountDto) {
        try {
            UUID uuid = UUID.fromString(shopId);
            Shop shop = shopRepository.findById(uuid)
                    .orElseThrow(() -> new RuntimeException("Shop not found with id: " + shopId));
            
            String stripeAccountId = stripeAccountDto.getStripeAccountId();
            if (stripeAccountId == null || stripeAccountId.isEmpty()) {
                logger.warning("Invalid shop ID or Stripe account ID");
                throw new IllegalArgumentException("Stripe account ID cannot be empty");
            }
            
            shop.setStripeAccountId(stripeAccountId);
            shopRepository.save(shop);
            
            logger.info(UPDATED_STRIPE_ACCOUNT_ID_FOR_SHOP + shopId);
            return "{\"message\": \"Stripe account updated successfully\"}";
        } catch (Exception e) {
            logger.severe("Error updating Stripe account ID: " + e.getMessage());
            throw new RuntimeException("Error updating Stripe account ID", e);
        }
    }
}
