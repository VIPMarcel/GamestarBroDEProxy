package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.UUID;

public class CoinsCommand extends Command {

    private final Proxy plugin;

    public CoinsCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {
                player.sendMessage(this.plugin.getPrefix() + "Du hast §a" + this.plugin.getDatabasePlayers().getCoins(player.getUniqueId()) + " Coins§7.");
                return;
            } else if(arguments.length == 1) {
                final String playerName = arguments[0];

                final UUID uuid = UUIDFetcher.getUUID(playerName);
                final String name = UUIDFetcher.getName(uuid);

                if(uuid == null) {
                    player.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
                    return;
                }

                if(!this.plugin.getDatabasePlayers().doesPlayerExists(uuid)) {
                    player.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist nicht registriert.");
                    return;
                }

                player.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7hat §a" + this.plugin.getDatabasePlayers().getCoins(uuid) + " Coins§7.");
            } else {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        } else {

            if(arguments.length == 0) {
                sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um seine Coins zu sehen.");
            } else if(arguments.length == 1) {
                final String playerName = arguments[0];

                final UUID uuid = UUIDFetcher.getUUID(playerName);
                final String name = UUIDFetcher.getName(uuid);

                if(uuid == null) {
                    sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
                    return;
                }

                if(!this.plugin.getDatabasePlayers().doesPlayerExists(uuid)) {
                    sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist nicht registriert.");
                    return;
                }

                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7hat §a" + this.plugin.getDatabasePlayers().getCoins(uuid) + " Coins§7.");
            } else {
                sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        }

    }

}
