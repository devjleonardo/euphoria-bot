package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.UserButtonInteractionEventGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserButtonInteractionEventUseCase {

    private final UserButtonInteractionEventGateway userButtonInteractionEventGateway;

    public void execute(ButtonInteractionEvent event) {
        userButtonInteractionEventGateway.execute(event);
    }

}
