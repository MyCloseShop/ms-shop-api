package com.etna.gpe.mycloseshop.ms_shop_api.controllers.shop;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.location.LocationDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.opening_hours.OpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.ShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.shop.UpdateStripeAccountDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Shop management", description = "API for managing shops")
@RequestMapping(path = "/shop")
public interface IShopController {
    @PostMapping
    @Operation(summary = "Create a shop", description = "Create a shop with the given information")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Shop created",
                            content = @Content(
                                    schema = @Schema(implementation = CreatedShopDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<CreatedShopDto> createShop(
            @Parameter(description = "Shop to create", required = true)
            @Valid @RequestBody CreateShopWithLocationAndOpeningHoursDto shopDto
    );

    @GetMapping
    @Operation(summary = "Get all shops", description = "Get all shops")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shops found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ShopDto.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<List<ShopDto>> getShops();

    @GetMapping(path = "/{shopId}")
    @Operation(summary = "Get a shop", description = "Get a shop by its id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shop found",
                            content = @Content(
                                    schema = @Schema(implementation = ShopDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shop not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<ShopDto> getShop(
            @Parameter(description = "Shop id", required = true)
            @PathVariable("shopId")
            String shopId
    );

    @GetMapping(path = "/{shopId}/opening-hours")
    @Operation(summary = "Get opening hours of a shop", description = "Get the opening hours of a shop by its id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Opening hours found",
                            content = @Content(
                                    mediaType = "application/json",
                                     array = @ArraySchema(
                                                schema = @Schema(implementation = OpeningHoursDto.class)
                                     )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shop not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<List<OpeningHoursDto>> getShopOpeningHours(
            @Parameter(description = "Shop id", required = true)
            @PathVariable("shopId")
            String shopId
    );

    @GetMapping(path = "/{shopId}/location")
    @Operation(summary = "Get location of a shop", description = "Get the location of a shop by its id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Location found",
                            content = @Content(
                                    schema = @Schema(implementation = LocationDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shop not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<LocationDto> getShopLocation(
            @Parameter(description = "Shop id", required = true)
            @PathVariable("shopId")
            String shopId
    );

    @GetMapping(path = "/{shopId}/stripe-account")
    @Operation(summary = "Get Stripe account ID of a shop", description = "Get the Stripe Connect account ID of a shop for payment processing")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stripe account ID found",
                            content = @Content(
                                    schema = @Schema(type = "object", example = "{\"stripeAccountId\": \"acct_1234567890\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shop not found or no Stripe account configured",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<String> getShopStripeAccountId(
            @Parameter(description = "Shop id", required = true)
            @PathVariable("shopId")
            String shopId
    );

    @PutMapping(path = "/{shopId}/stripe-account")
    @Operation(summary = "Update Stripe account ID of a shop", description = "Update the Stripe Connect account ID of a shop for payment processing")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Stripe account ID updated successfully",
                            content = @Content(
                                    schema = @Schema(type = "object", example = "{\"message\": \"Stripe account updated successfully\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Shop not found",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid Stripe account ID",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<String> updateShopStripeAccountId(
            @Parameter(description = "Shop id", required = true)
            @PathVariable("shopId")
            String shopId,
            @Parameter(description = "Stripe account ID", required = true)
            @RequestBody UpdateStripeAccountDto stripeAccountDto
    );
}
