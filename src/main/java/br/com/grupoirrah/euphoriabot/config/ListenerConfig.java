package br.com.grupoirrah.euphoriabot.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ListenerConfig {

    private final JDA jda;
    private final List<ListenerAdapter> listeners;

    @PostConstruct
    public void registerListeners() {
        for (ListenerAdapter listener : listeners) {
            jda.addEventListener(listener);
        }
    }

}
