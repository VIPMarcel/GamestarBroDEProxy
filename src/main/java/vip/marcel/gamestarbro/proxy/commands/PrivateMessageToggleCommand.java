package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class PrivateMessageToggleCommand extends Command {

    private final Proxy plugin;

    public PrivateMessageToggleCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length != 0) {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            if(this.plugin.getNotifyToggle().contains(player)) {
                this.plugin.getNotifyToggle().remove(player);
                player.sendMessage(this.plugin.getPrefix() + "§aDu empfängst nun wieder private Nachrichten.");
            } else {
                this.plugin.getNotifyToggle().add(player);
                player.sendMessage(this.plugin.getPrefix() + "§cDu empfängst nun keine privaten Nachrichten mehr.");
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
