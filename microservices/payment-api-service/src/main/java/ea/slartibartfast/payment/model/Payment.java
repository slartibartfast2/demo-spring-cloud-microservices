package ea.slartibartfast.payment.model;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {
    private Long id;
    private BigDecimal price;
    private PaymentChannel paymentChannel;
    private Card card;
    private Merchant merchant;

    public Payment(BigDecimal price, PaymentChannel paymentChannel) {
        this.price = price;
        this.paymentChannel = paymentChannel;
    }
}
