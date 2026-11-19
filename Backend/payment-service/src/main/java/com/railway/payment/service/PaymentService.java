package com.railway.payment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.railway.payment.client.ReservationServiceClient;
import com.railway.payment.dto.CreatePaymentRequest;
import com.railway.payment.dto.PaymentResponse;
import com.railway.payment.dto.PaymentResultRequest;
import com.railway.payment.dto.ReservationPaymentDetails;
import com.railway.payment.entity.Payment;
import com.railway.payment.entity.PaymentStatus;
import com.railway.payment.exception.PaymentException;
import com.railway.payment.repository.PaymentRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationServiceClient reservationServiceClient;

    @Value("${internal.api-key}")
    private String internalApiKey;

    public PaymentResponse processPayment(
            CreatePaymentRequest request,
            String passengerEmail) {

        ReservationPaymentDetails reservation;

        try {
            reservation = reservationServiceClient
                    .getReservationPaymentDetails(request.getReservationId());

        } catch (FeignException.NotFound ex) {
            throw new PaymentException("Reservation not found");
        }

        if (!reservation.getPassengerEmail().equalsIgnoreCase(passengerEmail)) {
            throw new PaymentException(
                    "You cannot pay for another passenger's reservation");
        }

        if (!"PENDING_PAYMENT".equals(reservation.getStatus())) {
            throw new PaymentException(
                    "This reservation is not awaiting payment");
        }

        if (paymentRepository.existsByReservationIdAndStatus(
                request.getReservationId(), PaymentStatus.SUCCESS)) {

            throw new PaymentException(
                    "Payment has already been completed for this reservation");
        }

        if (paymentRepository.existsByReservationIdAndStatus(
                request.getReservationId(), PaymentStatus.PENDING)) {

            throw new PaymentException(
                    "A payment is already being processed for this reservation");
        }

        Payment payment = Payment.builder()
                .reservationId(reservation.getReservationId())
                .pnr(reservation.getPnr())
                .passengerEmail(passengerEmail)
                .amount(reservation.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .transactionReference(
                        "TXN-" + UUID.randomUUID().toString()
                                .replace("-", "").substring(0, 12))
                .build();

        payment = paymentRepository.saveAndFlush(payment);

        boolean successful = !request.isSimulateFailure();

        try {
            reservationServiceClient.updatePaymentResult(
                    reservation.getReservationId(),
                    internalApiKey,
                    new PaymentResultRequest(
                            successful,
                            payment.getTransactionReference()));

        } catch (FeignException ex) {
            throw new PaymentException(
                    "Payment result could not be confirmed with Reservation Service. "
                    + "The payment is pending reconciliation.");
        }

        payment.setStatus(
                successful ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

        payment = paymentRepository.save(payment);

        return convertToResponse(payment);
    }

    public List<PaymentResponse> getMyPayments(String passengerEmail) {

        return paymentRepository
                .findByPassengerEmailOrderByCreatedAtDesc(passengerEmail)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private PaymentResponse convertToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getReservationId(),
                payment.getPnr(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionReference(),
                payment.getCreatedAt());
    }
}