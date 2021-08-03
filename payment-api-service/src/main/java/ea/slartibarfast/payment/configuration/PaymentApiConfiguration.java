package ea.slartibarfast.payment.configuration;

import ea.slartibarfast.payment.model.Payment;
import ea.slartibarfast.payment.model.PaymentChannel;
import ea.slartibarfast.payment.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@EnableDiscoveryClient
@OpenAPIDefinition(info =
    @Info(title = "Payment API", version = "1.0", description = "Documentation Payment API v1.0")
)
@Configuration
public class PaymentApiConfiguration {

    @Bean
    public PaymentRepository paymentRepository() {
        PaymentRepository paymentRepository = new PaymentRepository();
        paymentRepository.add(new Payment(BigDecimal.valueOf(100L), PaymentChannel.MOBILE));
        paymentRepository.add(new Payment(BigDecimal.ONE, PaymentChannel.WEB));
        return paymentRepository;
    }
}
