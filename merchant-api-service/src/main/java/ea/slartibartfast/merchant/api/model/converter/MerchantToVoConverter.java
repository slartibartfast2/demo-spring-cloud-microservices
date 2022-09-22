package ea.slartibartfast.merchant.api.model.converter;

import ea.slartibartfast.merchant.api.model.Merchant;
import ea.slartibartfast.merchant.api.model.vo.MerchantVo;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class MerchantToVoConverter implements Function<Merchant, MerchantVo> {

    @Override
    public MerchantVo apply(Merchant merchant) {
        return MerchantVo.builder()
                         .key(merchant.getKey())
                         .email(merchant.getEmail())
                         .password(merchant.getPassword())
                         .role(merchant.getRole())
                         .build();
    }
}
