package ea.slartibartfast.authorization.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantVo {

    private String key;
    private String email;
    private String password;
    private String role;
}
