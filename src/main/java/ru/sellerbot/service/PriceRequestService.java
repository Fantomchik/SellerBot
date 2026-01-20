package ru.sellerbot.service;

public interface PriceRequestService {
    /**
     * Ensure there is an OPEN request for this model and the user is subscribed as a waiter.
     * Sends message to manager (once) and stores manager messageId to correlate reply.
     */
    void ensureOpenRequestAndSubscribeWaiter(
            String modelKey,
            String modelDisplay,
            Long waiterChatId,
            Integer waiterMessageId
    );
}

