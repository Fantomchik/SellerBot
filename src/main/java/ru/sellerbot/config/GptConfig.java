package ru.sellerbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gpt")
public class GptConfig {
    private String systemPrompt;
    private String model;
    private String dealSystemPrompt;

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDealSystemPrompt() {
        return dealSystemPrompt;
    }

    public void setDealSystemPrompt(String dealSystemPrompt) {
        this.dealSystemPrompt = dealSystemPrompt;
    }
}