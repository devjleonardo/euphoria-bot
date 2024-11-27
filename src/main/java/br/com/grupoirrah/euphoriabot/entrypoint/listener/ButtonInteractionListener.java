package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.UserButtonInteractionUseCase;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ButtonInteractionListener extends ListenerAdapter {

    private final UserButtonInteractionUseCase userButtonInteractionUseCase;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        LogUtil.logInfo(log, "Recebido evento de interação com botão: ID do botão '{}', usuário '{}'",
                event.getButton().getId(), event.getUser().getAsTag());

        userButtonInteractionUseCase.execute(event);

        LogUtil.logInfo(log, "Interação com botão: ID do botão '{}', usuário '{}', processado com " +
                "sucesso!", event.getButton().getId(), event.getUser().getAsTag());
    }

}
