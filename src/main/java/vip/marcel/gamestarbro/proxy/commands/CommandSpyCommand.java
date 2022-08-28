package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class CommandSpyCommand extends Command {

    private final Proxy plugin;

    public CommandSpyCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {

                if(this.plugin.getCommandSpy().contains(player)) {
                    this.plugin.getCommandSpy().remove(player);
                    player.sendMessage(this.plugin.getPrefix() + "Du hast §eCommandSpy §cdeaktiviert§7.");
                } else {
                    this.plugin.getCommandSpy().add(player);
                    player.sendMessage(this.plugin.getPrefix() + "Du hast §eCommandSpy §aaktiviert§7, bis zum verlassen.");
                }

            } else {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDer Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
