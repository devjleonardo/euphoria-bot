package br.com.grupoirrah.euphoriabot.entrypoint.listener;

import br.com.grupoirrah.euphoriabot.core.usecase.interactor.CreateRoleUseCase;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GuildJoinListener extends ListenerAdapter {

    private final CreateRoleUseCase createRoleUseCase;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        LogUtil.logInfo(log, "Recebido evento de entrada em um servidor: {}", event.getGuild().getName());

        createRoleUseCase.execute(event);

        LogUtil.logInfo(log, "Evento de entrada no servidor '{}' processado com sucesso!",
                event.getGuild().getName());
    }

}
