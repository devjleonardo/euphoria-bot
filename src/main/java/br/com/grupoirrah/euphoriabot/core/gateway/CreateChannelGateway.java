package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public interface CreateChannelGateway {
    void createActivationChannel(GuildJoinEvent event, Role memberRole);
    void createWelcomeChannel(GuildJoinEvent event, Role memberRole);
}