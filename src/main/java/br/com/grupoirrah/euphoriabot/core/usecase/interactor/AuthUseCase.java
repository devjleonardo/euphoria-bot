package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoriabot.core.gateway.*;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.AuthStateOutput;
import br.com.grupoirrah.euphoriabot.core.usecase.boundary.output.UserProviderOutput;
import br.com.grupoirrah.euphoriabot.core.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserButtonInteractionGateway userButtonInteractionGateway;
    private final TokenProviderGateway tokenProviderGateway;
    private final UserProviderGateway userProviderGateway;
    private final GuildProviderGateway guildProviderGateway;
    private final MailboxValidationProviderGateway mailboxValidationProviderGateway;
    private final JDA jda;

    public Mono<Optional<String>> execute(String code, String state) throws Exception {
        String decodedState = URLDecoder.decode(state, StandardCharsets.UTF_8);
        AuthStateOutput authStateOutput = tokenProviderGateway.parseState(decodedState);

        InteractionHook hook = userButtonInteractionGateway.getInteractionCache()
            .remove(authStateOutput.interactionId());

        if (hook == null) {
            return Mono.just(Optional.of("❌ Interação não encontrada."));
        }

        return tokenProviderGateway.retrieveAccessToken(code)
            .flatMap(accessToken -> userProviderGateway.fetchUserProvider(accessToken)
                .flatMap(userInfo -> processUserInfo(authStateOutput, userInfo, hook))
            );
    }

    private Mono<Optional<String>> processUserInfo(AuthStateOutput authStateOutput,
                                                   UserProviderOutput userInfo,
                                                   InteractionHook hook) {
        Guild guild = guildProviderGateway.getGuildById(jda, authStateOutput.guildId());

        if (guild == null) {
            hook.sendMessage("❌ Servidor não encontrado.")
                .setEphemeral(true)
                .queue();
            return Mono.just(Optional.empty());
        }

        Member member = guildProviderGateway.getMemberById(guild, userInfo.id());
        if (member == null) {
            hook.sendMessage("❌ Membro não encontrado no servidor.")
                .setEphemeral(true)
                .queue();
            return Mono.just(Optional.empty());
        }

        return validateEmailAndAssignRole(guild, member, userInfo, hook);
    }

    private Mono<Optional<String>> validateEmailAndAssignRole(Guild guild,
                                                              Member member,
                                                              UserProviderOutput userInfo,
                                                              InteractionHook hook) {
        String email = userInfo.email();

        if (email.endsWith("@grupoirrah.com")) {
            return mailboxValidationProviderGateway.validateEmail(email)
                .flatMap(isValid -> {
                    if (isValid) {
                        guildProviderGateway.assignRoleToMember(guild, member, "✅ ┇ Membro");

                        String welcomeChannelName = "✨・bem-vindo";
                        String welcomeChannelId = guild.getTextChannelsByName(welcomeChannelName, true)
                            .stream()
                            .findFirst()
                            .map(channel -> channel.getId())
                            .orElse(null);

                        if (welcomeChannelId == null) {
                            LogUtil.logError(log, "❌ Canal com o nome '{}' não encontrado no " +
                                "servidor '{}'.", welcomeChannelName, guild.getName());
                            return Mono.just(Optional.of("❌ O canal de boas-vindas não foi encontrado. " +
                                "Entre em contato com um administrador."));
                        }

                        String intermediateMessage = """
                                🌟 **Tudo pronto!**
                                Sua conta foi ativada com sucesso, você já pode começar a explorar nosso servidor.
                                
                                👉 **Próximo passo:**
                                Clique no botão abaixo para acessar o canal de boas-vindas e conhecer tudo o que 
                                preparamos para você!
                                
                                _Estamos animados em ter você conosco! 😊_
                                """;

                        Button welcomeButton = Button.link(
                            "https://discord.com/channels/" + guild.getId() + "/" + welcomeChannelId,
                            "Acesse o canal de boas-vindas 🎉"
                        );

                        hook.sendMessage(intermediateMessage)
                            .addActionRow(welcomeButton)
                            .setEphemeral(true)
                            .queue();

                        return Mono.just(Optional.empty());
                    } else {
                        sendFriendlyDm(member, "Olá! Detectamos que o e-mail informado não pertence " +
                            "ao domínio do **Grupo Irrah**", hook, guild);
                        return Mono.just(Optional.empty());
                    }
                });
        } else {
            sendFriendlyDm(member, "❌ Apenas e-mails com o domínio **@grupoirrah.com** são permitidos", hook,
                guild);
            return Mono.just(Optional.empty());
        }
    }

    private void sendFriendlyDm(Member member, String message, InteractionHook hook, Guild guild) {
        if (member == null) {
            LogUtil.logWarn(log, "❌ Membro ou usuário é nulo. Não foi possível enviar DM.");
            return;
        } else {
            member.getUser();
        }

        String guildName = guild.getName();
        String fullMessage = message + " no servidor **" + guildName + "**.";

        member.getUser().openPrivateChannel().queue(
            channel -> channel.sendMessage(fullMessage).queue(
                success -> {
                    LogUtil.logInfo(log, "✅ Mensagem enviada com sucesso para o usuário: {}",
                        member.getUser().getId());

                    kickMember(guild, member);
                },
                error -> {
                    if (error.getMessage().contains("50007")) {
                        LogUtil.logError(log, "❌ Não foi possível enviar DM para o usuário: {}. " +
                            "Motivo: Usuário desativou mensagens diretas.", member.getUser().getId());

                        hook.sendMessage(fullMessage).setEphemeral(true).queue();
                    } else {
                        LogUtil.logError(log, "❌ Falha ao enviar a mensagem para o usuário: {}. " +
                            "Erro: {}", member.getUser().getId(), error.getMessage());
                    }
                }
            ),
            error -> LogUtil.logError(log, "❌ Não foi possível abrir o canal privado para " +
                "o usuário: {}. Motivo: {}", member.getUser().getId(), error.getMessage())
        );
    }

    private static void kickMember(Guild guild, Member member) {
        guild.kick(member).queue();
    }

}
