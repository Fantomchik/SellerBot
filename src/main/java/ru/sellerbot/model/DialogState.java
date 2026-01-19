package ru.sellerbot.model;

public enum DialogState {
    WAITING_MODEL,
    WAITING_PRICE,
    OFFER_SENT,
    NEGOTIATION,
    HANDOVER_TO_MANAGER,
    DONE
}
