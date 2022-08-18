package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class AbuseCommand extends Command {

    private final Proxy plugin;

    public AbuseCommand(Proxy plugin, String name) {
        super(name, "proxy.command.abuse");
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

    }

}
