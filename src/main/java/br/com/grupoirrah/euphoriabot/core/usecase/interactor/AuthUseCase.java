package br.com.grupoirrah.euphoriabot.core.usecase.interactor;

import br.com.grupoirrah.euphoria.core.util.EmailUtil;
import br.com.grupoirrah.euphoria.domain.model.OAuthCallbackData;
import br.com.grupoirrah.euphoria.domain.model.UserInfo;
import br.com.grupoirrah.euphoria.listener.ButtonInteractionListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private static final ConcurrentHashMap<String, InteractionHook> interactionCache =
        ButtonInteractionListener.getInteractionCache();

    private final TokenService tokenService;
    private final UserService userService;
    private final GuildService guildService;
    private final EmailValidationService emailValidationService;
    private final JDA jda;

    public Optional<String> execute(String code, String state) throws Exception {
        String decodedState = URLDecoder.decode(state, StandardCharsets.UTF_8);
        OAuthCallbackData callbackData = tokenService.parseState(decodedState);

        InteractionHook hook = interactionCache.remove(callbackData.interactionId());
        if (hook == null) {
            return Optional.of("❌ Interação não encontrada.");
        }

        String accessToken = tokenService.retrieveAccessToken(code);
        UserInfo userInfo = userService.fetchUserInfo(accessToken);

        if (!EmailUtil.isEmailFromGrupoIrrahDomain(userInfo.email())) {
            hook.sendMessage("❌ E-mail inválido! Utilize um e-mail com o domínio @grupoirrah.com.")
                .setEphemeral(true)
                .queue();
            return Optional.empty();
        }

        Guild guild = guildService.getGuildById(jda, callbackData.guildId());
        if (guild == null) {
            hook.sendMessage("❌ Servidor não encontrado.")
                .setEphemeral(true)
                .queue();
            return Optional.empty();
        }

        Member member = guildService.getMemberById(guild, userInfo.id());
        if (member == null) {
            hook.sendMessage("❌ Membro não encontrado no servidor.")
                .setEphemeral(true)
                .queue();
            return Optional.empty();
        }

        String email = userInfo.email();

        if (email.endsWith("@grupoirrah.com")) {
            boolean isValid = emailValidationService.validateEmail(email);
            if (isValid) {
                guildService.assignRoleToMember(guild, member, "✅ ┇ Membro");
                hook.sendMessage("✅ Validação de e-mail concluída e cargo atribuído com sucesso! 🎉")
                        .setEphemeral(true)
                        .queue();
            } else {
                sendFriendlyDm(member, "Olá! Detectamos que o e-mail informado não pertence ao domínio do " +
                        "Grupo Irrah. 😕 Por favor, utilize um e-mail válido com o domínio @grupoirrah.com. " +
                        "Se tiver dúvidas, estamos aqui para ajudar!");
                kickMember(guild, member, hook);
                hook.sendMessage("❌ O e-mail informado não pertence ao domínio @grupoirrah.com. Caso tenha dúvidas " +
                                "ou precise de suporte, entre em contato conosco!")
                        .setEphemeral(true)
                        .queue();
            }
        } else if (email.endsWith("@gmail.com")) {
            sendFriendlyDm(member, "❌ Não conseguimos validar seu e-mail Gmail. Caso tenha dúvidas, entre em contato conosco.");
            kickMember(guild, member, hook);
            hook.sendMessage("❌ Apenas e-mails com o domínio @grupoirrah.com são permitidos.")
                    .setEphemeral(true)
                    .queue();
        } else {
            hook.sendMessage("❌ Apenas e-mails com o domínio @grupoirrah.com são permitidos.")
                    .setEphemeral(true)
                    .queue();
            return Optional.empty();
        }

        return Optional.empty();
    }

    private void sendFriendlyDm(Member member, String message) {
        member.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage(message).queue();
        }, throwable -> {
            System.err.println("❌ Não foi possível enviar DM para o usuário: " + member.getUser().getId());
        });
    }

    private static void kickMember(Guild guild, Member member, InteractionHook hook) {
        guild.kick(member).queue(
                success -> {
                    hook.sendMessage("❌ E-mail inválido! Não conseguimos validar a existência de sua conta Gmail.")
                            .setEphemeral(true)
                            .queue();
                },
                error -> {
                    System.err.println("❌ Erro ao tentar remover o membro: " + error.getMessage());
                    hook.sendMessage("⚠️ Não foi possível remover o membro do servidor automaticamente.")
                            .setEphemeral(true)
                            .queue();
                }
        );
    }

}