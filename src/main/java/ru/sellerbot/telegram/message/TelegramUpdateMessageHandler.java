package ru.sellerbot.telegram.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sellerbot.command.TelegramCommandsDispatcher;
import ru.sellerbot.service.ConversationService;
import ru.sellerbot.service.ManagerReplyProcessingService;
import ru.sellerbot.service.TelegramAsyncMessageSender;
import ru.sellerbot.service.impl.ChatGptServiceImpl;


@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUpdateMessageHandler {
    private final TelegramCommandsDispatcher telegramCommandsDispatcher;
    private final TelegramAsyncMessageSender telegramAsyncMessageSender;
    private final TelegramVoiceHandler telegramVoiceHandler;
    private final ApplicationContext context;
    private final ChatGptServiceImpl chatGptService;
    private final ManagerReplyProcessingService managerReplyProcessingService;
    private final ConversationService conversationService;
    @Value("${telegram.manager.chat-id}")
    private Long managerChatId;
    @Value("${telegram.manager.user-id}")
    private Long managerUserId;

    public TelegramTextHandler getTelegramTextHandler() {
        return context.getBean(TelegramTextHandler.class);
    }

    public CallbackQueryHandler getCallbackQueryHandler() {
        return context.getBean(CallbackQueryHandler.class);
    }

    public BotApiMethod<?> handleMessage(Update update) {
        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var response = getCallbackQueryHandler().processCallbackQuery(callbackQuery);
            getCallbackQueryHandler().answerCallbackQuery(callbackQuery);
            return response;
        }

        if (!update.hasMessage()) {
            return null;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom() != null ? update.getMessage().getFrom().getId().longValue() : null;
            if (chatId.equals(managerChatId) && managerUserId != null && managerUserId != 0L && userId != null && userId.equals(managerUserId)) {
                // Обработка ответа менеджера
                return managerReplyProcessingService.processManagerReply(update.getMessage());
            }
            // Для всех остальных сообщений — всегда бизнес-логика с deal-system-prompt
            return conversationService.handleUserMessage(chatId, update.getMessage().getMessageId(), update.getMessage().getText());
        }

        var message = update.getMessage();
        if (telegramCommandsDispatcher.isCommand(message)) {
            return telegramCommandsDispatcher.processCommand(message);
        }

        var chatId = message.getChatId().toString();

        if (message.hasVoice() || message.hasText() || message.hasPhoto() || message.hasVideo() ||
                (message.hasDocument() && isImageDocument(message))) {
            telegramAsyncMessageSender.sendMessageAsync(
                    chatId,
                    () -> (SendMessage) handleMessageAsync(message),
                    (throwable) -> getErrorMessage(throwable, chatId)
            );
        }
        return null;
    }

    private BotApiMethod<?> handleMessageAsync(Message message) {
        if (message.hasVoice()) {
            return telegramVoiceHandler.processVoice(message);
        } else if (message.hasText()) {
            return getTelegramTextHandler().processTextMessage(message);
        }
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("Сейчас поддерживаются только текст и голос. Пожалуйста, отправьте сообщение текстом.")
                .build();
    }

    private SendMessage getErrorMessage(Throwable throwable, String chatId) {
        log.error("Error while processing message", throwable);
        return SendMessage.builder()
                .chatId(chatId)
                .text("Произошла ошибка при обработке сообщения. Пожалуйста, попробуйте еще раз.")
                .build();
    }

    private boolean isImageDocument(Message message) {
        var document = message.getDocument();
        if (document == null) return false;
        var mimeType = document.getMimeType();
        return mimeType != null && mimeType.startsWith("image/");
    }
}
