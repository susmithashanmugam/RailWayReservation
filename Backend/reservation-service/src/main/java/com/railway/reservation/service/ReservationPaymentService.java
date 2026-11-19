
package com.railway.reservation.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.railway.reservation.dto.PaymentResultRequest;
import com.railway.reservation.dto.ReservationPaymentDetailsResponse;
import com.railway.reservation.entity.Reservation;
import com.railway.reservation.entity.ReservationStatus;
import com.railway.reservation.event.BookingConfirmedEvent;
import com.railway.reservation.exception.ResourceNotFoundException;
import com.railway.reservation.repository.ReservationRepository;
import com.railway.reservation.repository.SeatAllocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationPaymentService {

    private final ReservationRepository reservationRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public ReservationPaymentDetailsResponse getReservationForPayment(
            Long reservationId,
            String passengerEmail) {

        Reservation reservation = reservationRepository
                .findByIdAndPassengerEmail(reservationId, passengerEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found"
                ));

        if (reservation.getStatus() != ReservationStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Reservation is not awaiting payment"
            );
        }

        return new ReservationPaymentDetailsResponse(
                reservation.getId(),
                reservation.getPnr(),
                reservation.getPassengerEmail(),
                reservation.getTotalAmount(),
                reservation.getStatus().name()
        );
    }

    @Transactional
    public void updatePaymentResult(
            Long reservationId,
            PaymentResultRequest request) {

        if (request == null || request.getSuccessful() == null) {
            throw new IllegalArgumentException(
                    "Payment result is required"
            );
        }

        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found"
                ));

        if (reservation.getStatus() != ReservationStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Reservation is not awaiting payment"
            );
        }

        if (Boolean.TRUE.equals(request.getSuccessful())) {

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);

            // Publish event after successful payment.
            // Listener processes notification after transaction commit.
            eventPublisher.publishEvent(
                    new BookingConfirmedEvent(
                            reservation.getPnr(),
                            reservation.getPassengerEmail()
                    )
            );

        } else {

            reservation.setStatus(ReservationStatus.CANCELLED);

            seatAllocationRepository.deleteByReservationId(reservationId);

            reservationRepository.save(reservation);
        }
    }
}
