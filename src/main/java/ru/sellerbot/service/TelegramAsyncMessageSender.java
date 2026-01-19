package ru.sellerbot.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Function;
import java.util.function.Supplier;

public interface TelegramAsyncMessageSender {

    void sendMessageAsync(
            String chatId,
            Supplier<SendMessage> action,
            Function<Throwable, SendMessage> onErrorHandler
    );
}
