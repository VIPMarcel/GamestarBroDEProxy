package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class AlertCommand extends Command {

    private final Proxy plugin;

    public AlertCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eNachricht §7eingeben, um sie auf dem gesammten Netzwerk zu teilen.");
            return;
        }

        String message  = "";
        for(int i = 0; i < arguments.length; i++) {
            message = message + arguments[i] + " ";
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
            players.sendMessage(" ");
            players.sendMessage("§8§l┃ §cRundruf §8► §7" + message);
            players.sendMessage(" ");
        }

        ProxyServer.getInstance().getConsole().sendMessage(" ");
        ProxyServer.getInstance().getConsole().sendMessage("§8§l┃ §cRundruf §8► §7" + message);
        ProxyServer.getInstance().getConsole().sendMessage(" ");

    }

}
