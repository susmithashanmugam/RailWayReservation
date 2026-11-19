
package com.railway.trainservice.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainSearchResponse {

    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String trainType;

    private Long sourceStationId;
    private String sourceStationName;
    private LocalTime departureTime;

    private Long destinationStationId;
    private String destinationStationName;
    private LocalTime arrivalTime;
}
