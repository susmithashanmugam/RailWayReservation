
package com.railway.reservation.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "Train ID is required")
    @Positive
    private Long trainId;

    @NotNull(message = "Source station ID is required")
    @Positive
    private Long sourceStationId;

    @NotNull(message = "Destination station ID is required")
    @Positive
    private Long destinationStationId;

    @NotNull(message = "Travel date is required")
    @FutureOrPresent(message = "Travel date cannot be in the past")
    private LocalDate travelDate;

    @NotBlank(message = "Travel class is required")
    private String travelClass;

    @NotEmpty(message = "At least one passenger is required")
    @Size(max = 6, message = "Maximum 6 passengers allowed per booking")
    @Valid
    private List<PassengerDetailRequest> passengers;
}
