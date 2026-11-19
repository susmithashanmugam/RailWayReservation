
package com.railway.reservation.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BookingNotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(BookingNotificationService.class);

    @Async("taskExecutor")
    public CompletableFuture<Void> sendBookingConfirmation(
            String pnr,
            String passengerEmail) {

        log.info(
                "Booking confirmation notification processed for PNR: {} and passenger: {}",
                pnr,
                passengerEmail
        );

        return CompletableFuture.completedFuture(null);
    }
}
