
package com.railway.reservation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.railway.reservation.dto.CreateReservationRequest;
import com.railway.reservation.dto.ReservationResponse;
import com.railway.reservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // Book a ticket
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse bookTicket(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return reservationService.bookTicket(
                request,
                jwt.getSubject()
        );
    }

    // Get all bookings of logged-in passenger
    @GetMapping("/my-bookings")
    public List<ReservationResponse> getMyBookings(
            @AuthenticationPrincipal Jwt jwt) {

        return reservationService.getMyBookings(
                jwt.getSubject()
        );
    }

    // Get booking using PNR
    @GetMapping("/pnr/{pnr}")
    public ReservationResponse getBookingByPnr(
             @PathVariable String pnr,
            @AuthenticationPrincipal Jwt jwt) {

        return reservationService.getBookingByPnr(
                pnr,
                jwt.getSubject()
        );
    }

    // Cancel booking
    @DeleteMapping("/pnr/{pnr}")
    public ReservationResponse cancelBooking(
            @PathVariable String pnr,
            @AuthenticationPrincipal Jwt jwt) {

        return reservationService.cancelBooking(
                pnr,
                jwt.getSubject()
        );
    }
}
