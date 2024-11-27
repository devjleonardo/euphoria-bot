package br.com.grupoirrah.euphoriabot.core.gateway;

import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.UserProviderOutput;
import reactor.core.publisher.Mono;

public interface UserProviderGateway {
    Mono<UserProviderOutput> fetchUserProvider(String accessToken);
}