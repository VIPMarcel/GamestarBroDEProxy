package vip.marcel.gamestarbro.proxy.discord.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

public class SlashCommandInteractionListener extends ListenerAdapter {

    private final Proxy plugin;

    public SlashCommandInteractionListener(Proxy plugin) {
        this.plugin = plugin;
    }

    public void onSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {

        if(event.getName().equalsIgnoreCase("minecraftname")) {
            OptionMapping nameOption = event.getOption("name");

            event.deferReply().queue();
            //do async

            if(!this.plugin.getDatabaseVerify().doesPlayerExists(nameOption.getAsMember().getId())) {
                event.getHook().sendMessage("Der Member " + nameOption.getAsMentionable() + " hat sich nicht mit Minecraft verifiziert.").queue();
                return;
            }

            event.getHook().sendMessage("Der Ingame-Name von " + nameOption.getAsMentionable() + " ist » '" + UUIDFetcher.getName(this.plugin.getDatabaseVerify().getUuid(nameOption.getAsMember().getId()))).setEphemeral(true).queue();
        }

    }

}
