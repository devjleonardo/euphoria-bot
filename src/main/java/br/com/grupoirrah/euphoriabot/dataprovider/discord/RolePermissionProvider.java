package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.RolePermissionProviderGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionProvider implements RolePermissionProviderGateway {

    private static final String MEMBER_ROLE_NAME = "✅ ┇ Membro";

    @Override
    public void updateChannelPermissionsForMemberRole(Guild guild, GuildChannel channel) {
        Role memberRole = findMemberRole(guild);
        if (memberRole == null) {
            LogUtil.logWarn(log, getRoleNotFoundMessage(guild.getName()));
            return;
        }

        channel.getPermissionContainer().upsertPermissionOverride(memberRole)
            .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)
            .queue(
                success -> LogUtil.logInfo(log, getPermissionUpdatedMessage(channel.getName())),

                error -> LogUtil.logException(log, getPermissionErrorMessage(channel.getName()),
                    (Exception) error)
            );
    }

    private String getRoleNotFoundMessage(String guildName) {
        return String.format("Cargo '%s' não encontrado no servidor '%s'.", MEMBER_ROLE_NAME, guildName);
    }

    private Role findMemberRole(Guild guild) {
        return guild.getRolesByName(MEMBER_ROLE_NAME, true).stream()
            .findFirst()
            .orElse(null);
    }

    private String getPermissionUpdatedMessage(String channelName) {
        return String.format("Permissões atualizadas para o cargo '%s' no canal '%s'.", MEMBER_ROLE_NAME, channelName);
    }

    private String getPermissionErrorMessage(String channelName) {
        return String.format("Erro ao atualizar permissões para o cargo '%s' no canal '%s'.", MEMBER_ROLE_NAME,
            channelName);
    }

}
