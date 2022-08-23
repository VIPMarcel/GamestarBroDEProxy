package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class BauserverCommand extends Command {

    private final Proxy plugin;

    public BauserverCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length != 0) {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(this.plugin.getBauServerName());

            player.connect(serverInfo);
            player.sendMessage(this.plugin.getPrefix() + "Du wirst auf den §eBauserver §7verschoben.");

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
            return;
        }

    }

}
