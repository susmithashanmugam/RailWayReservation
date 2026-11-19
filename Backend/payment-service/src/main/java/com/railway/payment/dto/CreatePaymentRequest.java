package com.railway.payment.dto;

import com.railway.payment.entity.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private boolean simulateFailure = false;
}