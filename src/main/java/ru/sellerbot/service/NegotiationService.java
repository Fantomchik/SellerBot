package ru.sellerbot.service;

import ru.sellerbot.dto.NegotiationResult;
import ru.sellerbot.model.PhonePrice;

public interface NegotiationService {
    NegotiationResult proposeBuyOffer(PhonePrice price);

    NegotiationResult negotiateSellOffer(PhonePrice price, int customerOffer);
}
