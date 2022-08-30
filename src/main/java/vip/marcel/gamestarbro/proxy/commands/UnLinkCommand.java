package vip.marcel.gamestarbro.proxy.commands;

import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.UUID;

public class UnLinkCommand extends Command {

    private final Proxy plugin;

    public UnLinkCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage("§8§l┃ §aVerify §8► §7" + "Du musst einen §eSpielernamen §7angeben, um seine Discord Verifizierung zu entfernen.");
            return;
        }

        if(arguments.length == 1) {

            final UUID uuid = UUIDFetcher.getUUID(arguments[0]);
            final String name = UUIDFetcher.getName(uuid);

            if(uuid == null) {
                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
                return;
            }

            if(!this.plugin.getDatabaseVerify().doesPlayerExists(uuid)) {
                sender.sendMessage("§8§l┃ §aVerify §8► §7" + "Der Spieler §e" + name + " §7ist nicht verifiziert.");
                return;
            }

            final User user = this.plugin.getDiscordStaffBOT().getJDA().getUserById(this.plugin.getDatabaseVerify().getUserId(uuid));

            if(this.plugin.getDiscordStaffBOT().removeVerifiedRole(user)) {
                this.plugin.getDatabaseVerify().deletePlayer(uuid);
                sender.sendMessage("§8§l┃ §aVerify §8► §7" + "Verifizierung von §e" + name + " §7gelöscht.");
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
        }

    }

}
