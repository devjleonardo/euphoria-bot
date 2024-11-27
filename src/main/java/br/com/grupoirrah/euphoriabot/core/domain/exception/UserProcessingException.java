package br.com.grupoirrah.euphoriabot.core.domain.exception;

public class UserProcessingException extends RuntimeException {

    public UserProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}
