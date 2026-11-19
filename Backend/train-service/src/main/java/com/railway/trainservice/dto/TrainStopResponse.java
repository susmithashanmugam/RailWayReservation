package com.railway.trainservice.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainStopResponse {

    private Long id;
    private Long stationId;
    private String stationCode;
    private String stationName;
    private String city;
    private Integer stopNumber;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Double distanceFromSource;
}

