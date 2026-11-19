package com.railway.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResultRequest {

    private Boolean successful;
    private String transactionReference;
}