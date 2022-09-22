package ea.slartibartfast.merchant.api.configuration;

import ea.slartibartfast.merchant.api.model.Merchant;
import ea.slartibartfast.merchant.api.repository.MerchantRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableDiscoveryClient
@OpenAPIDefinition(info =
    @Info(title = "Merchant API", version = "1.0", description = "Documentation Merchant API v1.0")
)
@Configuration
public class MerchantApiConfiguration {

    @Bean
    public MerchantRepository merchantRepository() {
        MerchantRepository merchantRepository = new MerchantRepository();
        merchantRepository.add(new Merchant("user1234", "user1@yahoo.com", "passw0rd", "USER"));
        merchantRepository.add(new Merchant("admin1234", "admin1@yahoo.com", "passw0rd!", "ADMIN"));
        return merchantRepository;
    }
}
