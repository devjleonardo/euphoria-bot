package br.com.grupoirrah.euphoriabot.entrypoint.http;

import br.com.grupoirrah.euphoria.core.util.LogUtil;
import br.com.grupoirrah.euphoria.domain.service.OAuthService;
import br.com.grupoirrah.euphoriabot.core.usecase.interactor.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthUseCase oAuthService;

    @GetMapping("/callback")
    public ResponseEntity<String> handleOAuthCallback(@RequestParam String code, @RequestParam String state) {
        try {
            Optional<String> errorMessage = oAuthService.handleOAuthCallback(code, state);

            if (errorMessage.isPresent()) {
                return createErrorResponse(400, errorMessage.get(), "Erro no callback OAuth: " +
                    errorMessage.get());
            }

            return ResponseEntity.status(200)
                .body("<script>window.close();</script>");
        } catch (Exception e) {
            return createErrorResponse(500, "Ocorreu um erro inesperado.",
                "Erro ao processar o callback OAuth.", e);
        }
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
