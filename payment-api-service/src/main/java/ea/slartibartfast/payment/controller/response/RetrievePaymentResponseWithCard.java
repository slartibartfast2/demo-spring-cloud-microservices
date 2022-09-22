package ea.slartibartfast.payment.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetrievePaymentResponseWithCard extends RetrievePaymentResponse {
    private String cardNumber;
    private String cardHolderName;
}
