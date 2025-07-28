package com.etna.gpe.mycloseshop.ms_shop_api.controllers.appointment;

import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentDto;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.AppointmentResponse;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.appointment.CreateAppointmentRequest;
import com.etna.gpe.mycloseshop.ms_shop_api.dtos.error.ResponseError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Appointment Managment", description = "API for managing appointments")
@RequestMapping(path = "/appointment")
public interface IAppointmentController {
    @GetMapping(path = "/shop/{shopId}/duration/{duration}")
    @Operation(summary = "Get available slots", description = "Get available slots for a service in a shop")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Slots found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = LocalTime.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request"
                    )
            }
    )
    ResponseEntity<List<LocalTime>> getAvailableSlots(
            @PathVariable String shopId,
            @PathVariable Integer duration,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );

    @PostMapping
    @Operation(summary = "Book an appointment", description = "Book an appointment for a service in a shop")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Appointment booked",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppointmentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<AppointmentDto> bookAppointment(@Valid @RequestBody CreateAppointmentRequest request);

    @GetMapping(path = "/shop/{shopId}")
    @Operation(summary = "Get appointments", description = "Get appointments for a shop")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointments found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = AppointmentResponse.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request"
                    )
            }
    )
    ResponseEntity<List<AppointmentDto>> getAppointmentsByShopId(@Valid @PathVariable UUID shopId);

    @GetMapping(path = "/user/{clientId}")
    @Operation(summary = "Get appointments", description = "Get appointments for a client")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointments found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = AppointmentDto.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request"
                    )
            }
    )
    ResponseEntity<List<AppointmentDto>> getAppointmentsByClientId(@Valid @PathVariable UUID clientId);

    // get client appointments by status
    @GetMapping(path = "/user/{clientId}/status/{status}")
    @Operation(summary = "Get appointments", description = "Get appointments for a client by status")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointments found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = AppointmentDto.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request"
                    )
            }
    )
    ResponseEntity<List<AppointmentDto>> getAppointmentsByClientIdAndStatus(
            @Valid @PathVariable UUID clientId, @Valid @PathVariable String status
    );

    // get shop appointments by status
    @GetMapping(path = "/shop/{shopId}/status/{status}")
    @Operation(summary = "Get appointments", description = "Get appointments for a shop by status")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointments found",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = AppointmentDto.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request"
                    )
            }
    )
    ResponseEntity<List<AppointmentDto>> getAppointmentsByShopIdAndStatus(
            @Valid @PathVariable UUID shopId, @Valid @PathVariable String status
    );

    // Paid appointment status
    @PatchMapping(path = "/paid/{appointmentId}")
    @Operation(summary = "Mark appointment as paid", description = "Mark an appointment as paid")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Appointment marked as paid",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseError.class)
                            )
                    )
            }
    )
    ResponseEntity<Boolean> paidAppointment(@Valid @PathVariable UUID appointmentId);

}
