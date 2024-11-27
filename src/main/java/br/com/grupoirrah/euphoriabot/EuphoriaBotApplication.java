package br.com.grupoirrah.euphoriabot;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EuphoriaBotApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();
		System.setProperty("DISCORD_BOT_TOKEN", dotenv.get("DISCORD_BOT_TOKEN"));
		System.setProperty("DISCORD_CLIENT_ID", dotenv.get("DISCORD_CLIENT_ID"));
		System.setProperty("DISCORD_CLIENT_SECRET", dotenv.get("DISCORD_CLIENT_SECRET"));

		String discordRedirectUri = dotenv.get("DISCORD_REDIRECT_URI");
		if (discordRedirectUri != null) {
			System.setProperty("DISCORD_REDIRECT_URI", discordRedirectUri);
		}

		System.setProperty("MAILBOXVALIDATOR_API_KEY", dotenv.get("MAILBOXVALIDATOR_API_KEY"));

		SpringApplication.run(EuphoriaBotApplication.class, args);
	}

}
