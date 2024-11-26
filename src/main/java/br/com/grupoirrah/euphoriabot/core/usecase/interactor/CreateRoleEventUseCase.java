package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.CreateChannelEventGateway;
import br.com.grupoirrah.euphoriabot.core.gateway.CreateRoleEventGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRoleEventUseCase {

    private final CreateRoleEventGateway createRoleEventGateway;
    private final CreateChannelEventGateway createChannelEventGateway;

    public void execute(GuildJoinEvent event) {
        createRoleEventGateway.createBotRole(event);
        createRoleEventGateway.createMemberRole(event, memberRole -> {
            createChannelEventGateway.createActivationChannel(event, memberRole);
            createChannelEventGateway.createWelcomeChannel(event, memberRole);
        });
    }

}
