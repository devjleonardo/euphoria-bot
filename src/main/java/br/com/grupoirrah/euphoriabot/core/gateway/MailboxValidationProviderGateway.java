package br.com.grupoirrah.euphoriabot.core.gateway;

import reactor.core.publisher.Mono;

public interface MailboxValidationProviderGateway {
    Mono<Boolean> validateEmail(String email);
}