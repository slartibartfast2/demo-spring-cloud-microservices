package ea.slartibartfast.card.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Card {
    private Long id;
    private Long paymentId;
    private String cardNumber;
    private String cardHolderName;

    public Card(Long paymentId, String cardNumber, String cardHolderName) {
        this.paymentId = paymentId;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }
}
