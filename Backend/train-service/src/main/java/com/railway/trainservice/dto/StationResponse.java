package com.railway.trainservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StationResponse {

    private Long id;
    private String stationCode;
    private String stationName;
    private String city;
    private String state;
    private boolean active;
}

