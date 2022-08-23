package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class NotifyToggleCommand extends Command {

    private final Proxy plugin;

    public NotifyToggleCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length != 0) {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            if(this.plugin.getStaffNotifyToggle().contains(player)) {
                this.plugin.getStaffNotifyToggle().remove(player);
                player.sendMessage(this.plugin.getPrefix() + "§aDu empfängst nun wieder §eTeamChat-§a, §eReport-§a, §eAbuse- §aund §ePardon §aNachrichten.");
            } else {
                this.plugin.getStaffNotifyToggle().add(player);
                player.sendMessage(this.plugin.getPrefix() + "§cDu empfängst nun keine §eTeamChat-§c, §eReport-§c, §eAbuse- §cund §ePardon §cNachrichten mehr.");
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
