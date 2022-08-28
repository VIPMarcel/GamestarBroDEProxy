package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class AlertDiscordCommand extends Command {

    private final Proxy plugin;

    public AlertDiscordCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eNachricht §7angeben, um sie auf Discord zu teilen.");
            return;
        }

        String message  = "";
        for(int i = 0; i < arguments.length; i++) {
            message = message + arguments[i] + " ";
        }

        String senderName = "Server | Automatisch";
        if(sender instanceof ProxiedPlayer player) {
            senderName = player.getName();
        }

        this.plugin.getDiscordStaffBOT().alertInformation(message, senderName);
        sender.sendMessage(this.plugin.getPrefix() + "§eDeine Nachricht wurde im Discord- Info- Kanal gesendet.");
    }

}
