package ea.slartibarfast.payment.controller.converter;

import ea.slartibarfast.payment.controller.response.RetrievePaymentResponse;
import ea.slartibarfast.payment.model.Payment;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PaymentToRetrievePaymentResponseConverter implements Function<Payment, RetrievePaymentResponse> {

    @Override
    public RetrievePaymentResponse apply(Payment payment) {
        RetrievePaymentResponse retrievePaymentResponse = new RetrievePaymentResponse();
        retrievePaymentResponse.setAmount(payment.getPrice());
        retrievePaymentResponse.setPaymentChannel(payment.getPaymentChannel().name());
        return retrievePaymentResponse;
    }
}
