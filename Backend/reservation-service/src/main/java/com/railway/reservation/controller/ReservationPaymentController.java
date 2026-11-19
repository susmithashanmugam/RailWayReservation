
package com.railway.reservation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.railway.reservation.dto.ReservationPaymentDetailsResponse;
import com.railway.reservation.service.ReservationPaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations/payment")
@RequiredArgsConstructor
public class ReservationPaymentController {

    private final ReservationPaymentService reservationPaymentService;

    @GetMapping("/{reservationId}")
    public ReservationPaymentDetailsResponse getReservationForPayment(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal Jwt jwt) {

        return reservationPaymentService.getReservationForPayment(
                reservationId,
                jwt.getSubject()
        );
    }
}
