package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public interface GuildProviderGateway {
    Guild getGuildById(JDA jda, String guildId);
    Member getMemberById(Guild guild, String userId);
    void assignRoleToMember(Guild guild, Member member, String roleName);
}