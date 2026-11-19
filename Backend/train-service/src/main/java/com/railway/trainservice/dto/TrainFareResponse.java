package com.railway.trainservice.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TrainFareResponse {

    private Long id;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String travelClass;
    private BigDecimal fareAmount;
}

