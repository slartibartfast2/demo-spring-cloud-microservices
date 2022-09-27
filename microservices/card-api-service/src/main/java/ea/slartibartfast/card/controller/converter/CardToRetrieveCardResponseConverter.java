package ea.slartibartfast.card.controller.converter;

import ea.slartibartfast.card.controller.response.RetrieveCardResponse;
import ea.slartibartfast.card.model.Card;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CardToRetrieveCardResponseConverter implements Function<Card, RetrieveCardResponse> {

    @Override
    public RetrieveCardResponse apply(Card card) {
        RetrieveCardResponse retrieveCardResponse = new RetrieveCardResponse();
        retrieveCardResponse.setCardHolderName(card.getCardHolderName());
        retrieveCardResponse.setCardNumberLast4Digits(getLast4Digits(card.getCardNumber()));
        return retrieveCardResponse;
    }

    private String getLast4Digits(String cardNumber) {
        if (cardNumber.length() > 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }

        return cardNumber;
    }
}
