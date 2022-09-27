package ea.slartibartfast.payment.controller.request;

import ea.slartibartfast.payment.model.PaymentChannel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Setter
@Getter
public class CreatePaymentRequest {

    @DecimalMin("0.0")
    @Digits(integer=30, fraction=8)
    private BigDecimal amount;

    private PaymentChannel paymentChannel;

    @NotBlank
    private String cardNumber;

    @NotBlank
    private String cardHolderName;

    @NotBlank
    private String merchantName;

    @NotBlank
    private String merchantKey;
}
