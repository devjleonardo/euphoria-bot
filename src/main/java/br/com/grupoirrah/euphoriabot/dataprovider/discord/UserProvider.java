package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.domain.exception.UserProcessingException;
import br.com.grupoirrah.euphoriabot.core.gateway.UserProviderGateway;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.UserProviderOutput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserProvider implements UserProviderGateway {

    private final WebClient webClient = WebClient.create("https://discord.com/api");
    private final ObjectMapper objectMapper;

    @Override
    public Mono<UserProviderOutput> fetchUserProvider(String accessToken) {
        return webClient.get()
            .uri("/users/@me")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(String.class)
            .map(this::parseUserInfo);
    }

    private UserProviderOutput parseUserInfo(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);
            return new UserProviderOutput(node.get("id").asText(), node.get("email").asText());
        } catch (Exception e) {
            String errorMessage = "Erro ao processar a resposta do usuário OAuth: " + response;
            throw new UserProcessingException(errorMessage, e);
        }
    }

}
