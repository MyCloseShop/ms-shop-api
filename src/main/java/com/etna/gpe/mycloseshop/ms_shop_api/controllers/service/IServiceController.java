package com.etna.gpe.mycloseshop.ms_shop_api.controllers.service;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreateServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.CreatedServiceDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.service.ServiceDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Service Managment", description = "API for managing services")
@RequestMapping(path = "/service")
public interface IServiceController {
    @PostMapping
    @Operation(summary = "Create a service", description = "Create a service with the given information")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Service created",
                            content = @Content(
                                    schema = @Schema(implementation = CreatedServiceDto.class)
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
    ResponseEntity<CreatedServiceDto> createServices(
            @Parameter(description = "Service to create", required = true)
            @Valid @RequestBody CreateServiceDto serviceDto
    );

    @GetMapping(path = "/shop/{shopId}")
    @Operation(summary = "Get all services from a shop", description = "Get all services from a shop with the given ID")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Services found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ServiceDto.class)
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
    ResponseEntity<List<ServiceDto>> getServicesOfShop(
            @Parameter(description = "Shop ID", required = true)
            @PathVariable
            @Valid String shopId
    );

    @GetMapping(path = "/{serviceId}")
    @Operation(summary = "Get a service", description = "Get a service by its id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Service found",
                            content = @Content(
                                    schema = @Schema(implementation = ServiceDto.class)
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
    ResponseEntity<ServiceDto> getServiceById(
            @Parameter(description = "Service ID", required = true)
            @Valid
            @PathVariable String serviceId
    );
}
