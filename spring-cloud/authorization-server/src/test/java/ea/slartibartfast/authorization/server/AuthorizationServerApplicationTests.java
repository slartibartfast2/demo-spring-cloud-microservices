package ea.slartibartfast.authorization.server;

import com.nimbusds.jose.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationServerApplicationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void requestTokenWhenUsingPasswordGrantTypeThenOk() throws Exception {

        this.mvc.perform(post("/oauth2/token")
                                 .param("grant_type", "client_credentials")
                                 .param("username", "slartibartfast")
                                 .param("password", "password")
                                 .header("Authorization", authorization()))
                .andExpect(status().isOk());
    }

    String authorization() {
        return "Basic " + Base64.encode("api-client:secret");
    }

}