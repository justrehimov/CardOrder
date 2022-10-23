package az.desofme.bank.controller;

import az.desofme.bank.dto.request.CardToCardRequest;
import az.desofme.bank.dto.response.ResponseModel;
import az.desofme.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping("/order")
    public ResponseModel<Object> orderCard(){
        return cardService.orderCard();
    }

    @PostMapping("/card-to-card")
    public ResponseModel<Object> cardToCard(@RequestBody CardToCardRequest cardToCardRequest){
        return cardService.cardToCard(cardToCardRequest);
    }

}
