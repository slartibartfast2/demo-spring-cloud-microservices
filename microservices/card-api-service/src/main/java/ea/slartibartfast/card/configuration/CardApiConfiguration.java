package ea.slartibartfast.card.configuration;

import ea.slartibartfast.card.model.Card;
import ea.slartibartfast.card.repository.CardRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info =
    @Info(title = "Card API", version = "1.0", description = "Documentation Card API v1.0")
)
@Configuration
public class CardApiConfiguration {

    @Bean
    public CardRepository cardRepository() {
        CardRepository cardRepository = new CardRepository();
        cardRepository.add(new Card(1L, "1111111111111111", "B A"));
        cardRepository.add(new Card(2L, "2222222222222222", "C A"));
        return cardRepository;
    }
}
