package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.domain.exception.AuthProcessingException;
import br.com.grupoirrah.euphoriabot.core.gateway.TokenProviderGateway;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.AuthStateOutput;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider implements TokenProviderGateway {

    private final WebClient webClient = WebClient.create("https://discord.com/api");
    private final ObjectMapper objectMapper;

    @Value("${discord.client.id}")
    private String clientId;

    @Value("${discord.client.secret}")
    private String clientSecret;

    @Value("${discord.redirect.uri}")
    private String redirectUri;

    @Override
    public Mono<String> retrieveAccessToken(String code) {
        return webClient.post()
            .uri("/oauth2/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .bodyValue("client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + redirectUri)
            .retrieve()
            .bodyToMono(String.class)
            .map(this::extractAccessToken);
    }
    @Override
    public AuthStateOutput parseState(String state) throws Exception {
        JsonNode node = objectMapper.readTree(state);
        return new AuthStateOutput(
            node.get("guildId").asText(),
            node.get("interactionId").asText()
        );
    }

    private String extractAccessToken(String tokenResponse) {
        try {
            JsonNode node = objectMapper.readTree(tokenResponse);
            return node.get("access_token").asText();
        } catch (Exception e) {
            String errorMessage = "Erro ao processar a resposta do token OAuth: " + tokenResponse;
            LogUtil.logException(log, errorMessage, e);
            throw new AuthProcessingException(errorMessage, e);
        }
    }

}
