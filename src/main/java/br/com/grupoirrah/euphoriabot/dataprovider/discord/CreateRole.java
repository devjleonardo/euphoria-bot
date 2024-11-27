
package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.CreateRoleGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.function.Consumer;

@Slf4j
@Component
public class CreateRole implements CreateRoleGateway {

    @Override
    public void createBotRole(GuildJoinEvent event) {
        event.getGuild().createRole()
                .setName("🤖 ┇ Assistente")
                .setHoisted(true)
                .setMentionable(false)
                .setPermissions(EnumSet.allOf(Permission.class))
                .queue(role -> assignRoleToBot(event, role));
    }

    @Override
    public void createMemberRole(GuildJoinEvent event, Consumer<Role> onRoleCreated) {
        event.getGuild().createRole()
                .setName("✅ ┇ Membro")
                .setMentionable(false)
                .setHoisted(true)
                .setPermissions(EnumSet.of(
                        Permission.VIEW_CHANNEL,
                        Permission.MESSAGE_SEND,
                        Permission.MESSAGE_HISTORY,
                        Permission.VOICE_CONNECT,
                        Permission.VOICE_SPEAK
                ))
                .queue(role -> {
                    updateChannelPermissions(event, role);
                    onRoleCreated.accept(role);
                });
    }

    private void assignRoleToBot(GuildJoinEvent event, Role role) {
        event.getGuild().addRoleToMember(event.getGuild().getSelfMember(), role).queue(
                success -> logRoleAssignmentSuccess(event, role),
                failure -> logRoleAssignmentFailure(event, role, failure)
        );
    }

    private void updateChannelPermissions(GuildJoinEvent event, Role role) {
        for (GuildChannel channel : event.getGuild().getChannels()) {
            channel.getPermissionContainer().upsertPermissionOverride(role)
                    .grant(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)
                    .queue(
                            success -> logPermissionUpdateSuccess(role, channel),
                            failure -> logPermissionUpdateFailure(role, channel, failure)
                    );

            channel.getPermissionContainer().upsertPermissionOverride(event.getGuild().getPublicRole())
                    .deny(Permission.VIEW_CHANNEL)
                    .queue(
                            success -> logPublicRolePermissionUpdateSuccess(channel),
                            failure -> logPublicRolePermissionUpdateFailure(channel, failure)
                    );
        }
    }

    private void logRoleAssignmentSuccess(GuildJoinEvent event, Role role) {
        LogUtil.logInfo(log, "✅ Cargo '{}' criado e atribuído ao bot no servidor '{}'.",
                role.getName(), event.getGuild().getName());
    }

    private void logRoleAssignmentFailure(GuildJoinEvent event, Role role, Throwable failure) {
        LogUtil.logError(log, "❌ Falha ao atribuir o cargo '{}' ao bot no servidor '{}'.",
                role.getName(), event.getGuild().getName(), failure);
    }

    private void logPermissionUpdateSuccess(Role role, GuildChannel channel) {
        LogUtil.logDebug(log, "✅ Permissões atualizadas para o cargo '{}' no canal '{}'.",
                role.getName(), channel.getName());
    }

    private void logPermissionUpdateFailure(Role role, GuildChannel channel, Throwable failure) {
        LogUtil.logError(log, "❌ Erro ao atualizar permissões para o cargo '{}' no canal '{}'.",
                role.getName(), channel.getName(), failure);
    }

    private void logPublicRolePermissionUpdateSuccess(GuildChannel channel) {
        LogUtil.logDebug(log, "✅ Permissões atualizadas para o cargo 'Público' no canal '{}'.",
                channel.getName());
    }

    private void logPublicRolePermissionUpdateFailure(GuildChannel channel, Throwable failure) {
        LogUtil.logError(log, "❌ Erro ao atualizar permissões para o cargo 'Público' no canal '{}'.",
                channel.getName(), failure);
    }

}
