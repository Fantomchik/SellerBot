package ru.sellerbot.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.sellerbot.model.enums.DialogState;
import ru.sellerbot.model.enums.IntentType;

@Getter
@Setter
@Builder
public class UserSession {
    private Long chatId;
    private DialogState state;
    private PhonePrice price;
    private String phoneModel;
    private Integer currentOffer;
    private IntentType intent;
}
