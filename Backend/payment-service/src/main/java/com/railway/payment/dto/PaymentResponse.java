package com.railway.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.railway.payment.entity.PaymentMethod;
import com.railway.payment.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long reservationId;
    private String pnr;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private LocalDateTime createdAt;
}