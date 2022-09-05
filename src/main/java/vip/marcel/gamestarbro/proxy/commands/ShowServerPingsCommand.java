package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class ShowServerPingsCommand extends Command {

    private final Proxy plugin;

    public ShowServerPingsCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(this.plugin.getShowServerPings().contains(player)) {
                this.plugin.getShowServerPings().remove(player);
                player.sendMessage(this.plugin.getPrefix() + "Du siehst nun nicht mehr, §ewer den Server in der Serverliste eingetragen §7hat.");
            } else {
                this.plugin.getShowServerPings().add(player);
                player.sendMessage(this.plugin.getPrefix() + "Du siehst nun, §ewer den Server in der Serverliste eingetragen §7hat.");
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
