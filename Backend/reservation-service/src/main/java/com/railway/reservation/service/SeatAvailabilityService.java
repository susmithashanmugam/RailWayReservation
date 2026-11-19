
package com.railway.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.railway.reservation.client.TrainServiceClient;
import com.railway.reservation.dto.SeatAvailabilityResponse;
import com.railway.reservation.dto.TrainResponse;
import com.railway.reservation.dto.TrainStopResponse;
import com.railway.reservation.entity.SeatAllocation;
import com.railway.reservation.repository.SeatAllocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatAvailabilityService {

    private final SeatAllocationRepository seatAllocationRepository;
    private final TrainServiceClient trainServiceClient;

    @Value("${reservation.default-seat-capacity:72}")
    private int seatCapacity;

    @Transactional(readOnly = true)
    public SeatAvailabilityResponse checkAvailability(
            Long trainId,
            Long sourceStationId,
            Long destinationStationId,
            LocalDate travelDate,
            String travelClass) {

        // Validate input
        if (trainId == null
                || sourceStationId == null
                || destinationStationId == null
                || travelDate == null
                || travelClass == null
                || travelClass.isBlank()) {

            throw new IllegalArgumentException(
                    "Train, stations, travel date and travel class are required"
            );
        }

        if (travelDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Travel date cannot be in the past"
            );
        }

        if (sourceStationId.equals(destinationStationId)) {
            throw new IllegalArgumentException(
                    "Source and destination stations cannot be the same"
            );
        }

        if (seatCapacity <= 0) {
            throw new IllegalStateException(
                    "Seat capacity must be greater than zero"
            );
        }

        String normalizedTravelClass =
                travelClass.trim().toUpperCase();

        // Verify train
        TrainResponse train =
                trainServiceClient.getTrainById(trainId);

        if (train == null || !train.isActive()) {
            throw new IllegalArgumentException(
                    "Train does not exist or is inactive"
            );
        }

        // Get train route
        List<TrainStopResponse> route =
                trainServiceClient.getTrainRoute(trainId);

        if (route == null || route.isEmpty()) {
            throw new IllegalArgumentException(
                    "Train route is not available"
            );
        }

        // Find source station
        TrainStopResponse source = route.stream()
                .filter(stop -> sourceStationId.equals(stop.getStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Source station is not on this train route"
                ));

        // Find destination station
        TrainStopResponse destination = route.stream()
                .filter(stop -> destinationStationId.equals(stop.getStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Destination station is not on this train route"
                ));

        if (source.getStopNumber() == null
                || destination.getStopNumber() == null
                || source.getStopNumber() >= destination.getStopNumber()) {

            throw new IllegalArgumentException(
                    "Destination must come after source"
            );
        }

        // Identify journey segments
        List<Integer> segments = java.util.stream.IntStream
                .range(
                        source.getStopNumber(),
                        destination.getStopNumber()
                )
                .boxed()
                .toList();

        // Find occupied seats on these segments
        List<SeatAllocation> allocations =
                seatAllocationRepository
                        .findByTrainIdAndTravelDateAndTravelClassAndSegmentNumberIn(
                                trainId,
                                travelDate,
                                normalizedTravelClass,
                                segments
                        );

        // Count distinct seats occupied on at least one journey segment
        Set<String> occupiedSeats = allocations.stream()
                .map(SeatAllocation::getSeatNumber)
                .collect(Collectors.toSet());

        int bookedSeats = occupiedSeats.size();

        int availableSeats = Math.max(
                0,
                seatCapacity - bookedSeats
        );

        return new SeatAvailabilityResponse(
                trainId,
                travelDate,
                normalizedTravelClass,
                sourceStationId,
                destinationStationId,
                seatCapacity,
                bookedSeats,
                availableSeats
        );
    }
}
