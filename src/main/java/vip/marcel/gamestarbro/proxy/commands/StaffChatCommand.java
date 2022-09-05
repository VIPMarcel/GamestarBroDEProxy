package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class StaffChatCommand extends Command {

    private final Proxy plugin;

    public StaffChatCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(this.plugin.getStaffNotifyToggle().contains(player)) {
                player.sendMessage(this.plugin.getPrefix() + "§cWeil du alle Team- Nachrichten ausgeschaltet hast, darfst du nicht in den TeamChat schreiben.");
                return;
            }

            if(arguments.length == 0) {
                player.sendMessage(this.plugin.getPrefix() + "Du musst eine §eNachricht §7eingeben, um sie in den TeamChat zu schreiben.");
                return;
            }

            String message = "";
            for(int i = 0; i < arguments.length; i++) {
                message = message + arguments[i] + " ";
            }

            message = ChatColor.translateAlternateColorCodes('&', message);

            String finalMessage = message;
            ProxyServer.getInstance().getPlayers().forEach(players -> {
                if(this.plugin.getOnlineStaff().contains(players)) {
                    if(!this.plugin.getStaffNotifyToggle().contains(players)) {
                        players.sendMessage(this.plugin.getTeamPrefix() + "§e" + player.getServer().getInfo().getName() + " §8┃ §c" + player.getName() + "§8 » §7" + finalMessage);
                    }
                }
            });

            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§e" + player.getServer().getInfo().getName() + " §8┃ §c" + player.getName() + "§8 » §7" + finalMessage);

        } else {

            if(arguments.length == 0) {
                sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eNachricht §7eingeben, um sie in den TeamChat zu schreiben.");
                return;
            }

            String message = "";
            for(int i = 0; i < arguments.length; i++) {
                message = message + arguments[i] + " ";
            }

            message = ChatColor.translateAlternateColorCodes('&', message);

            String finalMessage = message;
            ProxyServer.getInstance().getPlayers().forEach(players -> {
                if(this.plugin.getOnlineStaff().contains(players)) {
                    if(!this.plugin.getStaffNotifyToggle().contains(players)) {
                        players.sendMessage(this.plugin.getTeamPrefix() + "§cKonsole" + "§8 » §7" + finalMessage);
                    }
                }
            });

            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§cKonsole" + "§8 » §7" + finalMessage);

        }

    }

}
