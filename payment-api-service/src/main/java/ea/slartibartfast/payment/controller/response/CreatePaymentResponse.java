package ea.slartibartfast.payment.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CreatePaymentResponse {
    private final String status;
}
