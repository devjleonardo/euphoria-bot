package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.GuildProviderGateway;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildProvider implements GuildProviderGateway {

    public Guild getGuildById(JDA jda, String guildId) {
        return jda.getGuildById(guildId);
    }

    public Member getMemberById(Guild guild, String userId) {
        return guild.retrieveMemberById(userId).complete();
    }

    public void assignRoleToMember(Guild guild, Member member, String roleName) {
        Role role = guild.getRolesByName(roleName, true).stream().findFirst()
            .orElseThrow(() -> new RuntimeException("Cargo '" + roleName + "' não encontrado."));
        guild.addRoleToMember(member, role).queue();
    }

}
