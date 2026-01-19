package ru.sellerbot.dto;

import ru.sellerbot.model.NegotiationStatus;

public record NegotiationResult(
        NegotiationStatus status,
        Integer counterOffer,
        String comment
) {
    public static NegotiationResult accept(Integer offer) {
        return new NegotiationResult(NegotiationStatus.ACCEPT, offer, null);
    }

    public static NegotiationResult reject(String comment) {
        return new NegotiationResult(NegotiationStatus.REJECT, null, comment);
    }

    public static NegotiationResult counter(Integer counterOffer, String comment) {
        return new NegotiationResult(NegotiationStatus.COUNTER, counterOffer, comment);
    }
}
