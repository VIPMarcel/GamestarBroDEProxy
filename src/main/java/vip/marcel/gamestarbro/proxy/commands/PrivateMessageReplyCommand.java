package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class PrivateMessageReplyCommand extends Command {

    private final Proxy plugin;

    public PrivateMessageReplyCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(!this.plugin.getPrivateMessageChannel().containsKey(player) | this.plugin.getPrivateMessageChannel().get(player) == null) {
                player.sendMessage(this.plugin.getPrefix() + "§7Du hast keine zu beantwortenden Nachrichten.");
                return;
            }
            final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(this.plugin.getPrivateMessageChannel().get(player).getName());

            if(target == null) {
                player.sendMessage(this.plugin.getPrefix() + "§7Der Spieler ist nicht mehr online.");
                return;
            }

            if(this.plugin.getNotifyToggle().contains(target)) {
                if(!this.plugin.getOnlineStaff().contains(player)) {
                    player.sendMessage(this.plugin.getPrefix() + "§cDu darfst diesem Spieler momentan keine privaten Nachrichten senden.");
                    return;
                }
            }

            String message = "";
            for(int i = 0; i < arguments.length; i++) {
                message = message + arguments[i] + " ";
            }

            if(this.plugin.getOnlineStaff().contains(player)) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            this.plugin.getPrivateMessageChannel().put(target, player);
            player.sendMessage("§8§l┃ §aMSG §8► " + player.getDisplayName() + " §8➟ " + target.getDisplayName() + " §8» §7" + message);
            target.sendMessage("§8§l┃ §aMSG §8► " + player.getDisplayName() + " §8➟ " + target.getDisplayName() + " §8» §7" + message);

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
