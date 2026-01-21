package ru.sellerbot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.sellerbot.dto.GptDealResponse;
import ru.sellerbot.service.TelegramBotSender;
import ru.sellerbot.service.TelegramManagerNotifierService;

@Service
@RequiredArgsConstructor
public class TelegramManagerNotifierServiceImpl implements TelegramManagerNotifierService {
    private final TelegramBotSender telegramBotSender;
    @Override
    public void notifyManager(Long managerChatId, GptDealResponse gptResponse) {
        StringBuilder sb = new StringBuilder();
        sb.append("Требуется помощь менеджера!\n");
        sb.append("Бренд: ").append(gptResponse.getLookupRequest() != null ? gptResponse.getLookupRequest().getBrand() : "?").append("\n");
        sb.append("Модель: ").append(gptResponse.getLookupRequest() != null ? gptResponse.getLookupRequest().getModel() : "?").append("\n");
        sb.append("Память: ").append(gptResponse.getLookupRequest() != null ? gptResponse.getLookupRequest().getStorage_gb() : "?").append("\n");
        sb.append("Комментарий: ").append(gptResponse.getMessageToUser() != null ? gptResponse.getMessageToUser() : "-");
        telegramBotSender.send(SendMessage.builder().chatId(managerChatId).text(sb.toString()).build());
    }
}
