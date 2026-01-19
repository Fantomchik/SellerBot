package ru.sellerbot.service;

public interface CommandStateService {
    void setStartCommandState(Long chatId, boolean state);
    boolean isStartCommandActive(Long chatId);
    void clearStartCommandState(Long chatId);
}
