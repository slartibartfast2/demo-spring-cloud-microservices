package ea.slartibarfast.card.controller;

import ea.slartibarfast.card.controller.response.RetrieveCardResponse;
import ea.slartibarfast.card.manager.CardManager;
import ea.slartibarfast.card.model.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CardController {

    private final CardManager cardManager;

    @GetMapping("/payment/{paymentId}")
    public Card retrieveCardByPayment(@PathVariable("paymentId") Long paymentId) {
        return cardManager.retrieveCardByPayment(paymentId);
    }
}
