package br.com.fiap.mobilidade.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cidadesEsgOpenApi(@Value("${app.environment:local}") String ambiente) {
        return new OpenAPI().info(new Info()
                .title("Cidades ESG Inteligentes - Mobilidade Sustentavel API")
                .description("API ESG de mobilidade sustentavel. Ambiente: " + ambiente.toUpperCase())
                .version("1.0.0"));
    }
}
