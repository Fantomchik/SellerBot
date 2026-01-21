package ru.sellerbot.service;

import ru.sellerbot.dto.GptDealResponse;

public interface TelegramManagerNotifierService {
    void notifyManager(Long managerChatId, GptDealResponse gptResponse);
}
