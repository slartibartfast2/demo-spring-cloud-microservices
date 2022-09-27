package ea.slartibartfast.payment.client;

import ea.slartibartfast.payment.model.Card;

public interface CardClient {
    Card findByPayment(Long paymentId);
}
