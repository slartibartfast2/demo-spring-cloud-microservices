package ea.slartibarfast.auth.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AuthApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApiServiceApplication.class, args);
    }

}
