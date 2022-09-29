package ea.slartibartfast.authorization.server.client;

import ea.slartibartfast.authorization.server.model.MerchantVo;

import java.util.Optional;

public interface MerchantClient {
    Optional<MerchantVo> findByMerchantId(Long id);
}
