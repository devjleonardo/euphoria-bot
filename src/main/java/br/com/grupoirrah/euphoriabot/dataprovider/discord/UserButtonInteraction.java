package br.com.grupoirrah.euphoriabot.dataprovider.discord;

import br.com.grupoirrah.euphoriabot.core.gateway.UserButtonInteractionGateway;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UserButtonInteraction implements UserButtonInteractionGateway {

    private final ConcurrentHashMap<String, InteractionHook> interactionCache = new ConcurrentHashMap<>();

    @Override
    public void execute(ButtonInteractionEvent event) {
        if ("activate-account".equals(event.getButton().getId())) {
            handleAccountActivation(event);
        }
    }

    private void handleAccountActivation(ButtonInteractionEvent event) {
        try {
            Long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
            event.deferReply(true).queue();

            InteractionHook hook = event.getHook();
            interactionCache.put(event.getId(), hook);

            String stateJson = new ObjectMapper().writeValueAsString(Map.of(
                    "guildId", guildId,
                    "interactionId", event.getId()
            ));

            String redirectUrl = "https://discord.com/oauth2/authorize"
                    + "?client_id=1308575817176973363"
                    + "&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth%2Fcallback"
                    + "&response_type=code"
                    + "&scope=email%20identify"
                    + "&state=" + URLEncoder.encode(stateJson, StandardCharsets.UTF_8);

            hook.sendMessage("Por favor, clique no link abaixo para validar seu e-mail:")
                    .addActionRow(Button.link(redirectUrl, "Validar e-mail").withEmoji(Emoji.fromUnicode("✉️")))
                    .setEphemeral(true)
                    .queue();

        } catch (Exception e) {
            LogUtil.logException(log, "Erro ao gerar o link de ativação: ", e);
            event.getHook().sendMessage("❌ Ocorreu um erro ao gerar o link de ativação.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public ConcurrentHashMap<String, InteractionHook> getInteractionCache() {
        return interactionCache;
    }

}
