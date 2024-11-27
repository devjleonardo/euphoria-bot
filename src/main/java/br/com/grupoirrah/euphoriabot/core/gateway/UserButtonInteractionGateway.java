package br.com.grupoirrah.euphoriabot.core.gateway;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.concurrent.ConcurrentHashMap;

public interface UserButtonInteractionGateway {
    void execute(ButtonInteractionEvent event);
    ConcurrentHashMap<String, InteractionHook> getInteractionCache();
}