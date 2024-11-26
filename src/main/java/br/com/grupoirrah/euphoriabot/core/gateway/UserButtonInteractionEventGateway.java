package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface UserButtonInteractionEventGateway {
    void execute(ButtonInteractionEvent event);
}