package ea.slartibartfast.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PaymentApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApiServiceApplication.class, args);
    }

}
