package ru.sellerbot.model;

public enum NegotiationStatus {
    ACCEPT,
    COUNTER,
    REJECT;

    public boolean isTerminal() {
        return this == ACCEPT || this == REJECT;
    }
}
