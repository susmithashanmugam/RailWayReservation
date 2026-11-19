package com.railway.trainservice.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTrainRequest {

    @NotBlank(message = "Train number is required")
    @Size(max = 10, message = "Train number cannot exceed 10 characters")
    private String trainNumber;

    @NotBlank(message = "Train name is required")
    @Size(max = 100, message = "Train name cannot exceed 100 characters")
    private String trainName;

    @NotBlank(message = "Train type is required")
    @Size(max = 50, message = "Train type cannot exceed 50 characters")
    private String trainType;
}
