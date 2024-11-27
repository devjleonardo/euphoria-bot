package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.RolePermissionUseCase;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelCreateListener extends ListenerAdapter {

    private final RolePermissionUseCase rolePermissionUseCase;

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (!(event.getChannel() instanceof GuildChannel channel)) {
            LogUtil.logWarn(log, "Evento de criação ignorado, pois o canal não é um GuildChannel. " +
                "Canal: {}", event.getChannel().getName());

            return;
        }

        try {
            LogUtil.logInfo(log, "Novo canal criado: {}. Atualizando permissões para o cargo de membro.",
                channel.getName());

            rolePermissionUseCase.updateChannelPermissionsForMemberRole(event.getGuild(), channel);

            LogUtil.logInfo(log, "Permissões atualizadas com sucesso no canal: {}.", channel.getName());
        } catch (Exception e) {
            LogUtil.logException(log, "Erro ao atualizar permissões no canal: {}", e);
        }
    }

}
