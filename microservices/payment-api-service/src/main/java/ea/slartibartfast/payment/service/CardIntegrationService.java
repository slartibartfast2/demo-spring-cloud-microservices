package ea.slartibartfast.payment.service;

import ea.slartibartfast.payment.client.CardClient;
import ea.slartibartfast.payment.exception.CardNotFoundException;
import ea.slartibartfast.payment.model.Card;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CardIntegrationService {

    private final CardClient cardClient;

    @Retry(name = "cardService")
    @CircuitBreaker(name = "cardService", fallbackMethod = "getCardFallbackValue")
    //@TimeLimiter(name = "cardService") not supported on feign clients currently
    public Card retrievePaymentWithCard(Long id, int delay, int faultPercent) {
        return cardClient.findByPayment(id, delay, faultPercent);
    }

    private Card getCardFallbackValue(Long cardId, int delay, int faultPercent, CallNotPermittedException ex) {

        log.warn("Creating a fallback product for cardId = {}", cardId);

        if (cardId == 13) {
            String errMsg = "Card Id: " + cardId + " not found in fallback cache!";
            log.warn(errMsg);
            throw new CardNotFoundException(errMsg);
        }

        return Card.builder().id(cardId).cardNumber("1111111111111111").cardHolderName("Joe Fallback").build();
    }
}
