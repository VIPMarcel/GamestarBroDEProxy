package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class PingCommand extends Command {

    private final Proxy plugin;

    public PingCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {

            if(sender instanceof ProxiedPlayer player) {
                player.sendMessage(this.plugin.getPrefix() + "Dein Ping beträgt §e" + player.getPing() + "ms§7.");
            } else {
                sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um seinen Ping zu sehen.");
            }

        } else if(arguments.length == 1) {
            final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[0]);

            if(target == null) {
                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7ist nicht online.");
                return;
            }

            sender.sendMessage(this.plugin.getPrefix() + "Der Ping von §e" + target.getName() + " §7beträgt §e" + target.getPing() + "ms§7.");

        } else {
            sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
        }

    }

}
