package az.desofme.bank.service;

import az.desofme.bank.dto.request.CardToCardRequest;
import az.desofme.bank.dto.response.ResponseModel;

public interface CardService {
    ResponseModel<Object> orderCard();

    ResponseModel<Object> cardToCard(CardToCardRequest cardToCardRequest);
}
