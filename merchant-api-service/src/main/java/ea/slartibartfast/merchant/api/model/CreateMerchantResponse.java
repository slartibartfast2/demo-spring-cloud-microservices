package ea.slartibartfast.merchant.api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMerchantResponse {
    private final String status;
}
