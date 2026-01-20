package ru.sellerbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "telegram.manager")
public class ManagerConfig {
    /**
     * Chat id where manager answers (private chat with the bot).
     */
    private Long chatId;

    /**
     * Optional manager user id for extra safety.
     */
    private Long userId;
}

