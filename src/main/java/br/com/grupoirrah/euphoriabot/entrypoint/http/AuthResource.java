package br.com.grupoirrah.euphoriabot.entrypoint.http;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.AuthUseCase;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthUseCase authUseCase;

    @GetMapping("/callback")
    public Mono<ResponseEntity<String>> handleOAuthCallback(@RequestParam String code,
                                                            @RequestParam String state) throws Exception {
        return authUseCase.execute(code, state)
            .map(errorMessage -> errorMessage
                .map(msg -> createErrorResponse(400, msg, "Erro no callback OAuth: " + msg))
                .orElse(ResponseEntity.status(200).body("<script>window.close();</script>"))
            )
            .onErrorResume(e -> {
                Exception exception = e instanceof Exception ? (Exception) e : new Exception(e);
                return Mono.just(createErrorResponse(500, "Ocorreu um erro inesperado.",
                    "Erro ao processar o callback OAuth.", exception));
            });
    }

    private ResponseEntity<String> createErrorResponse(int status, String message, String logMessage) {
        return createErrorResponse(status, message, logMessage, null);
    }

    private ResponseEntity<String> createErrorResponse(int status, String message, String logMessage, Exception e) {
        if (e != null) {
            LogUtil.logException(log, logMessage, e);
        } else {
            LogUtil.logWarn(log, logMessage);
        }

        String body = String.format("<script>alert('%s'); window.close();</script>", message);
        return ResponseEntity.status(status).body(body);
    }

}
