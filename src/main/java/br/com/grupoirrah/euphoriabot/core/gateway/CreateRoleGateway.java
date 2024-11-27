package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import java.util.function.Consumer;

public interface CreateRoleGateway {
    void createBotRole(GuildJoinEvent event);
    void createMemberRole(GuildJoinEvent event, Consumer<Role> onRoleCreated);
}