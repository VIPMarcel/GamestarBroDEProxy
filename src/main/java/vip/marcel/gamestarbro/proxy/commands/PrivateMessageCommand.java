package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrivateMessageCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public PrivateMessageCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {
                player.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um ihm private Nachrichten zu schreiben.");
                player.sendMessage(this.plugin.getPrefix() + "§8» §7Wenn du keine privaten Nachrichten mehr empfangen möchtest, nutze §e/msgtoggle§7.");
                return;
            }

            final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[0]);

            if(target == null) {
                player.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7ist nicht online.");
                return;
            }

            if(target.equals(player)) {
                player.sendMessage(this.plugin.getPrefix() + "§7Du darfst dir selbst keine privaten Nachrichten schreiben.");
                return;
            }

            if(arguments.length == 1) {
                player.sendMessage(this.plugin.getPrefix() + "Du musst eine §eNachricht §7eingeben, um sie §e" + target.getName() + " §7zu schicken.");
                return;
            }

            String message = "";
            for(int i = 1; i < arguments.length; i++) {
                message = message + arguments[i] + " ";
            }

            if(this.plugin.getOnlineStaff().contains(player)) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            if(this.plugin.getNotifyToggle().contains(target)) {
                if(!plugin.getOnlineStaff().contains(player)) {
                    player.sendMessage(this.plugin.getPrefix() + "§cDu darfst diesem Spieler momentan keine privaten Nachrichten senden.");
                    return;
                }
            }

            this.plugin.getPrivateMessageChannel().put(target, player);
            player.sendMessage("§8§l┃ §aMSG §8► " + player.getDisplayName() + " §8➟ " + target.getDisplayName() + " §8» §7" + message);
            target.sendMessage("§8§l┃ §aMSG §8► " + player.getDisplayName() + " §8➟ " + target.getDisplayName() + " §8» §7" + message);

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(arguments.length == 1) {
            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                if(sender.getName() != players.getName()) {
                    output.add(players.getName());
                }
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
