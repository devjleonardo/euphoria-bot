package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.RolePermissionProviderGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionUseCase {

    private final RolePermissionProviderGateway rolePermissionProviderGateway;

    public void updateChannelPermissionsForMemberRole(Guild guild, GuildChannel channel) {
        rolePermissionProviderGateway.updateChannelPermissionsForMemberRole(guild, channel);
    }

}
