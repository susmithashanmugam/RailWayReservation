
package com.railway.trainservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class TrainStopRequest {

    @NotNull(message = "Station ID is required")
    @Positive(message = "Station ID must be positive")
    private Long stationId;

    @NotNull(message = "Stop number is required")
    @Positive(message = "Stop number must be positive")
    private Integer stopNumber;

    private LocalTime arrivalTime;

    private LocalTime departureTime;

    @NotNull(message = "Distance from source is required")
    @DecimalMin(value = "0.0", message = "Distance cannot be negative")
    private BigDecimal distanceFromSource;
}
