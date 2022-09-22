package ea.slartibartfast.auth.api.client;

import ea.slartibartfast.auth.api.model.AuthRequest;
import ea.slartibartfast.auth.api.model.vo.MerchantVo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "mock.merchant.client.enabled", havingValue = "true")
public class MockMerchantClient implements MerchantClient {

    @Override
    public MerchantVo retrieveById(Long merchantId) {
        return MerchantVo.builder().key("x").email("john.doe@system.com").password("pass").build();
    }

    @Override
    public MerchantVo createMerchantAuth(AuthRequest authRequest) {
        return MerchantVo.builder().key("x")
                         .email(authRequest.getEmail())
                         .password(authRequest.getPassword())
                         .role("mock-manager")
                         .build();
    }
}
