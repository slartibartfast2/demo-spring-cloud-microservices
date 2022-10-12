package ea.slartibartfast.card.manager;

import ea.slartibartfast.card.model.Card;
import ea.slartibartfast.card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@RequiredArgsConstructor
@Slf4j
@Service
public class CardManager {

    private final CardRepository cardRepository;

    public Card retrieveCardByPayment(Long paymentId, int delay, int faultPercent) {
        log.info("Will get card info for paymentId={}", paymentId);
        if (delay > 0) {
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return cardRepository.findByPayment(paymentId)
                         .map(e -> throwErrorIfBadLuck(e, faultPercent))
                         .orElse(null);
    }

    private Card throwErrorIfBadLuck(Card card, int faultPercent) {
        if (faultPercent == 0) {
            return card;
        }

        int randomThreshold = getRandomNumber(1, 100);
        if (faultPercent < randomThreshold) {
            log.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);

        } else {
            log.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }

        return card;
    }

    private final Random randomNumberGenerator = new Random();

    private int getRandomNumber(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}
