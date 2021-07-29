package ea.slartibarfast.card.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetrieveCardResponse {
    private String cardHolderName;
    private String cardNumberLast4Digits;
}
