package ea.slartibarfast.card.manager;

import ea.slartibarfast.card.model.Card;
import ea.slartibarfast.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class CardManager {

    private final CardRepository cardRepository;

    public Card retrieveCardByPayment(Long paymentId) {
       return cardRepository.findByPayment(paymentId);
    }
}
