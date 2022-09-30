package ea.slartibartfast.payment.client;

import ea.slartibartfast.payment.model.Card;

public interface CardClient {
    Card findByPayment(long paymentId,  int delay, int faultPercent);
}
