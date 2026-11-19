
package com.railway.reservation.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.railway.reservation.dto.SeatAvailabilityResponse;
import com.railway.reservation.service.SeatAvailabilityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class AvailabilityController {

    private final SeatAvailabilityService seatAvailabilityService;

    @GetMapping("/availability")
    public SeatAvailabilityResponse checkAvailability(
            @RequestParam Long trainId,
            @RequestParam Long sourceStationId,
            @RequestParam Long destinationStationId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate travelDate,
            @RequestParam String travelClass) {

        return seatAvailabilityService.checkAvailability(
                trainId,
                sourceStationId,
                destinationStationId,
                travelDate,
                travelClass
        );
    }
}
