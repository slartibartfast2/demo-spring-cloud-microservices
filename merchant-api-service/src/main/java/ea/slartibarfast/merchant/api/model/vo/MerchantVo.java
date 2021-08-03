package ea.slartibarfast.merchant.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MerchantVo {

    private String key;
    private String email;
    private String password;
    private String role;
}
