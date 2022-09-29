package ea.slartibartfast.authorization.server.client;

import ea.slartibartfast.authorization.server.model.MerchantVo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "merchant-api-service")
@ConditionalOnProperty(value = "mock.merchant.client.enabled", havingValue = "false", matchIfMissing = true)
public interface FeignMerchantClient extends MerchantClient {

    @GetMapping("/merchant/{id}")
    Optional<MerchantVo> findByMerchantId(@PathVariable("id") Long id);
}
