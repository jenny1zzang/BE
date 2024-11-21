package SJUCapstone.BE;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("https://e4u.kro.kr"); // HTTPS 프로토콜 사용
        server.setDescription("Production Server");

        return new OpenAPI()
                .addServersItem(server);
    }

}
