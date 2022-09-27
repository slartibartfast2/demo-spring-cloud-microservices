package ea.slartibartfast.payment.client;

import ea.slartibartfast.payment.model.Card;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "mock.card.client.enabled", havingValue = "true")
public class MockCardClient implements CardClient {

    @Override
    public Card findByPayment(Long paymentId) {
        return Card.builder().id(-1L).cardNumber("1111222233334444").cardHolderName("John Doe").build();
    }
}
