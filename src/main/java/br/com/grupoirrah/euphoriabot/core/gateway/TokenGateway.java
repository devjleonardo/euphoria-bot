package br.com.grupoirrah.euphoriabot.core.gateway;

import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.ParsedAuthStateOutput;

public interface TokenGateway {
    String retrieveAccessToken(String code);

    ParsedAuthStateOutput parseState(String state) throws Exception;
}