package ea.slartibarfast.payment.controller.converter;

import ea.slartibarfast.payment.controller.request.CreatePaymentRequest;
import ea.slartibarfast.payment.model.Card;
import ea.slartibarfast.payment.model.Merchant;
import ea.slartibarfast.payment.model.Payment;
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
