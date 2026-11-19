
package com.railway.trainservice.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railway.trainservice.dto.TrainSearchResponse;
import com.railway.trainservice.service.TrainSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainSearchController {

    private final TrainSearchService trainSearchService;

    // Search trains by source and destination stations
    @GetMapping("/search")
    public CompletableFuture<List<TrainSearchResponse>> searchTrains(
            @RequestParam Long sourceStationId,
            @RequestParam Long destinationStationId) {

        return trainSearchService.searchTrains(
                sourceStationId,
                destinationStationId
        );
    }
}
