
package com.railway.reservation.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.railway.reservation.event.BookingConfirmedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingNotificationListener {

    private final BookingNotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingConfirmed(BookingConfirmedEvent event) {

        notificationService.sendBookingConfirmation(
                event.pnr(),
                event.passengerEmail()
        );
    }
}
