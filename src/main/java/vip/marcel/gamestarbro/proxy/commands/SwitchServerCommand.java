package vip.marcel.gamestarbro.proxy.commands;

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

public class SwitchServerCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public SwitchServerCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {
                player.sendMessage(this.plugin.getPrefix() + "Du musst einen §eServernamen §7eingeben, um dich zu verbinden.");
                return;
            }
            final String serverName = arguments[0];

            if(!ProxyServer.getInstance().getServers().containsKey(serverName)) {
                player.sendMessage(this.plugin.getPrefix() + "Der Server §e" + serverName + " §7ist nicht registriert.");
                return;
            }

            if(serverName.equalsIgnoreCase(this.plugin.getBauServerName())) {
                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "bauserver");
                return;
            }

            if(serverName.equalsIgnoreCase(this.plugin.getDevServerName())) {
                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "devserver");
                return;
            }

            if(player.getServer().getInfo().equals(ProxyServer.getInstance().getServerInfo(serverName))) {
                player.sendMessage(this.plugin.getPrefix() + "§cDu bist bereits auf diesem Server.");
                return;
            }

            player.connect(ProxyServer.getInstance().getServerInfo(serverName));
            player.sendMessage(this.plugin.getPrefix() + "Du wirst auf den Server §e" + serverName + " §7verschoben.");

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(sender.hasPermission("proxy.staff")) {
            if(arguments.length == 1) {
                for(String servers : ProxyServer.getInstance().getServers().keySet()) {
                    output.add(servers);
                }
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
