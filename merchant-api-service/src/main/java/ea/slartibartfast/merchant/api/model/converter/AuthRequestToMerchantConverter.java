package ea.slartibartfast.merchant.api.model.converter;

import ea.slartibartfast.merchant.api.model.AuthRequest;
import ea.slartibartfast.merchant.api.model.Merchant;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AuthRequestToMerchantConverter implements Function<AuthRequest, Merchant> {

    @Override
    public Merchant apply(AuthRequest authRequest) {
        return new Merchant(prepareMerchantKey(authRequest.getFirstName(), authRequest.getLastName()), authRequest.getEmail(), authRequest.getPassword(),
                            "USER");
    }

    private String prepareMerchantKey(String firstName, String lastName) {
        return String.join("-", firstName, lastName);
    }
}
