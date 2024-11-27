package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public interface RolePermissionProviderGateway {
    void updateChannelPermissionsForMemberRole(Guild guild, GuildChannel channel);
}