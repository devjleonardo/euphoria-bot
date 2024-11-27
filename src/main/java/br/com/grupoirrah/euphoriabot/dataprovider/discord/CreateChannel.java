package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.CreateChannelGateway;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@Component
public class CreateChannel implements CreateChannelGateway {

    @Override
    public void createActivationChannel(GuildJoinEvent event, Role memberRole) {
        String categoryName = "╭──── 🛡️ ✦ VALIDAÇÃO";
        String channelName = "🔑・ativar-conta";

        adjustExistingCategories(event);

        event.getGuild().createCategory(categoryName).queue(category -> {
            moveCategoryToPosition(category, 0); // Coloca no topo

            category.createTextChannel(channelName).queue(channel -> {
                IPermissionContainer permissions = channel.getPermissionContainer();

                permissions.upsertPermissionOverride(memberRole)
                        .deny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)
                        .queue();

                permissions.upsertPermissionOverride(event.getGuild().getPublicRole())
                        .grant(Permission.VIEW_CHANNEL)
                        .deny(Permission.MESSAGE_SEND)
                        .queue();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("🛡️ Ativação de Conta Necessária");
                embed.setDescription("Para garantir a segurança do servidor, precisamos que você ative sua conta " +
                        "antes de começar a interagir conosco.");

                embed.addField("📢 Como ativar?", "Clique no botão abaixo para prosseguir com a " +
                        "ativação da sua conta.", false);

                embed.setColor(new Color(0x5865F2));
                embed.setFooter("Estamos ansiosos para te receber na comunidade!", null);

                channel.sendMessageEmbeds(embed.build())
                        .setActionRow(Button.primary("activate-account", "Ativar Conta")
                                .withEmoji(Emoji.fromUnicode("🛡️")))
                        .queue();
            });
        });
    }

    @Override
    public void createWelcomeChannel(GuildJoinEvent event, Role memberRole) {
        String categoryName = "╭──── 🎉 ✦ BOAS-VINDAS";
        String channelName = "✨・bem-vindo";

        adjustExistingCategories(event);

        event.getGuild().createCategory(categoryName).queue(category -> {
            moveCategoryToPosition(category, 1);

            category.createTextChannel(channelName).queue(channel -> {
                IPermissionContainer permissions = channel.getPermissionContainer();

                permissions.upsertPermissionOverride(memberRole)
                        .grant(Permission.VIEW_CHANNEL)
                        .deny(Permission.MESSAGE_SEND)
                        .queue();

                permissions.upsertPermissionOverride(event.getGuild().getPublicRole())
                        .deny(Permission.VIEW_CHANNEL)
                        .queue();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("🎉 Bem-vindo(a) à Comunidade!");
                embed.setDescription("Olá! Estamos felizes em ter você aqui. Aproveite a comunidade!");

                embed.addField("🌟 Agora você faz parte de algo incrível!", "Estamos muito felizes " +
                        "em ter você conosco. Explore os canais, participe das conversas e aproveite ao máximo nossa " +
                        "comunidade!", false);

                embed.setColor(new Color(0x00FF00));
                embed.setFooter("Euphoria App - Estamos sempre aqui para você!", null);

                channel.sendMessageEmbeds(embed.build()).queue();
            });
        });
    }

    private void adjustExistingCategories(GuildJoinEvent event) {
        List<Category> existingCategories = event.getGuild().getCategories();
        for (int i = 0; i < existingCategories.size(); i++) {
            Category category = existingCategories.get(i);
            category.getManager().setPosition(i + 2).queue();
        }
    }

    private void moveCategoryToPosition(Category category, int position) {
        category.getManager().setPosition(position).queue();
    }

}
