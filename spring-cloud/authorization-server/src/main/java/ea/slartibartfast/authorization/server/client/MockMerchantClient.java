package ea.slartibartfast.authorization.server.client;

import ea.slartibartfast.authorization.server.model.MerchantVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(value = "mock.merchant.client.enabled", havingValue = "true")
public class MockMerchantClient implements MerchantClient {

    @Override
    public Optional<MerchantVo> findByMerchantId(Long id) {
        log.info("mock");
        MerchantVo merchantVo = null;
        if (id == -1L) {
            merchantVo = MerchantVo.builder()
                                   .key("1111222233334444")
                                   .email("slartibartfast@gmail.com")
                                   .password("pass")
                                   .role("USER")
                                   .build();
        }

        return Optional.ofNullable(merchantVo);
    }
}
