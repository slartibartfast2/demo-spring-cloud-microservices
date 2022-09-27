package ea.slartibartfast.card.repository;

import ea.slartibartfast.card.model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    private List<Card> cards = new ArrayList<>();

    public Card add(Card card) {
        card.setId((long) (cards.size()+1));
        cards.add(card);
        return card;
    }

    public Card findById(Long id) {
        return cards.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Card> findAll() {
        return cards;
    }

    public Card findByPayment(Long paymentId) {
        return cards.stream().filter(c -> c.getPaymentId().equals(paymentId)).findFirst().orElse(null);
    }
}
