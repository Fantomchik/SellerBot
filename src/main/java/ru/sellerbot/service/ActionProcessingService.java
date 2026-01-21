package ru.sellerbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sellerbot.dto.GptDealResponse;

public interface ActionProcessingService {
    SendMessage processAction(Long chatId, GptDealResponse gptResponse);
}
