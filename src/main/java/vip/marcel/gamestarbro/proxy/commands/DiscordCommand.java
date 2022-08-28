package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class DiscordCommand extends Command {

    private final Proxy plugin;

    public DiscordCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Unser Discord- Server §8» §e" + "https://discord.gg/rrtCWGhrrV");
        } else {
            sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
        }

    }

}
