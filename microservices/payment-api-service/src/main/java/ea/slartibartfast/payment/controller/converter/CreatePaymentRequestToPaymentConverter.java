package ea.slartibartfast.payment.controller.converter;

import ea.slartibartfast.payment.controller.request.CreatePaymentRequest;
import ea.slartibartfast.payment.model.Card;
import ea.slartibartfast.payment.model.Merchant;
import ea.slartibartfast.payment.model.Payment;
import ea.slartibartfast.payment.model.PaymentChannel;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CreatePaymentRequestToPaymentConverter implements Function<CreatePaymentRequest, Payment> {

    @Override
    public Payment apply(CreatePaymentRequest createPaymentRequest) {
        Payment payment = new Payment();
        payment.setPrice(createPaymentRequest.getAmount());
        payment.setPaymentChannel(createPaymentRequest.getPaymentChannel());
        payment.setCard(prepareCard(createPaymentRequest));
        payment.setMerchant(prepareMerchant(createPaymentRequest));
        return payment;
    }

    private Card prepareCard(CreatePaymentRequest createPaymentRequest) {
        Card card = new Card();
        card.setCardNumber(createPaymentRequest.getCardNumber());
        card.setCardHolderName(createPaymentRequest.getCardHolderName());
        return card;
    }

    private Merchant prepareMerchant(CreatePaymentRequest createPaymentRequest) {
        Merchant merchant = new Merchant();
        merchant.setKey(createPaymentRequest.getMerchantKey());
        merchant.setName(createPaymentRequest.getMerchantName());
        return merchant;
    }
}
