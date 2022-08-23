package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class LobbyCommand extends Command {

    private final Proxy plugin;

    public LobbyCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length != 0){
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            if(player.getServer().getInfo().getName().equalsIgnoreCase(this.plugin.getLobbyServerName())) {
                player.sendMessage(this.plugin.getPrefix() + "§cDu befindest dich bereits in der Lobby.");
                return;
            }

            player.connect(ProxyServer.getInstance().getServerInfo(this.plugin.getLobbyServerName()));
            player.sendMessage(this.plugin.getPrefix() + "§eDu wirst auf die Lobby verschoben.");

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
