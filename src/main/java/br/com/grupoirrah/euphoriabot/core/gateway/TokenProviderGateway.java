package br.com.grupoirrah.euphoriabot.core.gateway;

import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.AuthStateOutput;
import reactor.core.publisher.Mono;

public interface TokenProviderGateway {
    Mono<String> retrieveAccessToken(String code);
    AuthStateOutput parseState(String state) throws Exception;
}