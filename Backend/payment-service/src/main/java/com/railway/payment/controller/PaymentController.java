package com.railway.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.railway.payment.dto.CreatePaymentRequest;
import com.railway.payment.dto.PaymentResponse;
import com.railway.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse makePayment(
            @Valid @RequestBody CreatePaymentRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return paymentService.processPayment(
                request,
                jwt.getSubject());
    }

    @GetMapping("/my-payments")
    public List<PaymentResponse> getMyPayments(
            @AuthenticationPrincipal Jwt jwt) {

        return paymentService.getMyPayments(jwt.getSubject());
    }
}