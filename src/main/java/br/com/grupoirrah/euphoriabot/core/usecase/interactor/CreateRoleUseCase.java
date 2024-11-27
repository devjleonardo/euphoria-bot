package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.CreateChannelGateway;
import br.com.grupoirrah.euphoriabot.core.gateway.CreateRoleGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final CreateRoleGateway createRoleGateway;
    private final CreateChannelGateway createChannelGateway;

    public void execute(GuildJoinEvent event) {
        createRoleGateway.createBotRole(event);
        createRoleGateway.createMemberRole(event, memberRole -> {
            createChannelGateway.createActivationChannel(event, memberRole);
            createChannelGateway.createWelcomeChannel(event, memberRole);
        });
    }

}
