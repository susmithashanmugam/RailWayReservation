package com.railway.payment.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPaymentDetails {

    private Long reservationId;
    private String pnr;
    private String passengerEmail;
    private BigDecimal totalAmount;
    private String status;
}