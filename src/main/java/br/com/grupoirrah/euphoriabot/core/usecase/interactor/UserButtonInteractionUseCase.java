package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.UserButtonInteractionGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserButtonInteractionUseCase {

    private final UserButtonInteractionGateway userButtonInteractionGateway;

    public void execute(ButtonInteractionEvent event) {
        userButtonInteractionGateway.execute(event);
    }

}
