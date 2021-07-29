package ea.slartibarfast.payment.controller.converter;

import ea.slartibarfast.payment.controller.response.RetrievePaymentResponseWithCard;
import ea.slartibarfast.payment.model.Payment;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PaymentToRetrievePaymentResponseWithCardConverter implements Function<Payment, RetrievePaymentResponseWithCard> {

    @Override
    public RetrievePaymentResponseWithCard apply(Payment payment) {
        RetrievePaymentResponseWithCard retrievePaymentResponseWithCard = new RetrievePaymentResponseWithCard();
        retrievePaymentResponseWithCard.setAmount(payment.getPrice());
        retrievePaymentResponseWithCard.setPaymentChannel(payment.getPaymentChannel().name());
        retrievePaymentResponseWithCard.setCardNumber(payment.getCard().getCardNumber());
        retrievePaymentResponseWithCard.setCardHolderName(payment.getCard().getCardHolderName());
        return retrievePaymentResponseWithCard;
    }
}
