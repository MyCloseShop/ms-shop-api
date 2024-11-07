package com.etna.gpe.mycloseshop.ms_shop_api.controllers;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.CreateShopWithLocationAndOpeningHoursDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.CreatedShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.ShopDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
                                    schema = @Schema(implementation = ShopDto.class)
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

}
