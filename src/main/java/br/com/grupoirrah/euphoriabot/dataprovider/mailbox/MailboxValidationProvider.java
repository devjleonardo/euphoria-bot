package br.com.grupoirrah.euphoriabot.dataprovider.mailbox;

import br.com.grupoirrah.euphoriabot.core.gateway.MailboxValidationProviderGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import com.mailboxvalidator.MBVResult;
import com.mailboxvalidator.SingleValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MailboxValidationProvider implements MailboxValidationProviderGateway {

    @Value("${mailboxvalidator.api.key}")
    private String apiKey;

    @Override
    public Mono<Boolean> validateEmail(String email) {
        return Mono.fromCallable(() -> {
            SingleValidation validator = new SingleValidation(apiKey);

            try {
                MBVResult result = validator.ValidateEmail(email);

                return "True".equalsIgnoreCase(result.getIsSMTP()) &&
                    "True".equalsIgnoreCase(result.getIsVerified()) &&
                    "True".equalsIgnoreCase(result.getIsSyntax());
            } catch (Exception e) {
                LogUtil.logException(log, "Erro ao validar o e-mail: {}", e);
                return false;
            }
        });
    }

}
