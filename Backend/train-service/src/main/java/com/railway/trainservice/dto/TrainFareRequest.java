package com.railway.trainservice.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TrainFareRequest {

    @NotBlank(message = "Travel class is required")
    @Size(max = 20, message = "Travel class cannot exceed 20 characters")
    private String travelClass;

    @NotNull(message = "Fare amount is required")
    @DecimalMin(value = "0.01", message = "Fare must be greater than zero")
    private BigDecimal fareAmount;
}
