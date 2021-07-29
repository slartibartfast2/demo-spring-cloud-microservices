package ea.slartibarfast.payment.model;

import lombok.Data;

@Data
public class Card {
    private Long id;
    private String cardNumber;
    private String cardHolderName;
}
