package ea.slartibartfast.payment.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RetrievePaymentResponse {
    private BigDecimal amount;
    private String paymentChannel;
}
