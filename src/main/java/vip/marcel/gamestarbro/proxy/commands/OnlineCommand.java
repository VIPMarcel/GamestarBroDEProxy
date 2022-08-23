package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class OnlineCommand extends Command {

    private final Proxy plugin;

    public OnlineCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length != 0) {
            sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            return;
        }

        final int onlinePlayersAmount = ProxyServer.getInstance().getOnlineCount() + this.plugin.getFakePlayers();

        if(onlinePlayersAmount == 1) {
            sender.sendMessage(this.plugin.getPrefix() + "Es ist momentan §e1 Spieler §7online.");
        } else {
            sender.sendMessage(this.plugin.getPrefix() + "Es sind momentan §e" + onlinePlayersAmount + " Spieler §7online.");
        }

    }

}
