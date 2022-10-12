package ea.slartibartfast.merchant.api.manager;

import ea.slartibartfast.merchant.api.model.AuthRequest;
import ea.slartibartfast.merchant.api.model.CreateMerchantResponse;
import ea.slartibartfast.merchant.api.model.Merchant;
import ea.slartibartfast.merchant.api.model.converter.AuthRequestToMerchantConverter;
import ea.slartibartfast.merchant.api.model.converter.MerchantToVoConverter;
import ea.slartibartfast.merchant.api.model.vo.MerchantVo;
import ea.slartibartfast.merchant.api.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class MerchantManager {

    private final MerchantRepository merchantRepository;
    private final MerchantToVoConverter merchantToVoConverter;
    private final AuthRequestToMerchantConverter authRequestToMerchantConverter;

    public MerchantVo retrieveMerchant(Long id) {
        log.info("Will get merchant info for id={}", id);
        Merchant merchant = merchantRepository.findById(id);
        return merchantToVoConverter.apply(merchant);
    }

    public List<MerchantVo> retrieveAllMerchants() {
        List<Merchant> merchants = merchantRepository.findAll();
        return merchants.stream().map(merchantToVoConverter).collect(Collectors.toList());
    }

    public CreateMerchantResponse createMerchant(AuthRequest authRequest) {
        Merchant merchant = authRequestToMerchantConverter.apply(authRequest);
        merchantRepository.add(merchant);
        return CreateMerchantResponse.builder().status("SUCCESS").build();
    }
}
