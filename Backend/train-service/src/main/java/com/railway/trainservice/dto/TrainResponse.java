package com.railway.trainservice.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainResponse {

    private Long id;
    private String trainNumber;
    private String trainName;
    private String trainType;
    private boolean active;
}
