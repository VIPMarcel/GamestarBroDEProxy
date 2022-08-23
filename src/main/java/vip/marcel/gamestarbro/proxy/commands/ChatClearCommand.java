package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class ChatClearCommand extends Command {

    private final Proxy plugin;

    public ChatClearCommand(Proxy plugin, String name, String permission) {
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

            player.getServer().getInfo().getPlayers().forEach(players -> {
                if(!this.plugin.getOnlineStaff().contains(players)) {
                    for(int i = 0; i < 600; i++) {
                        players.sendMessage(" ");
                    }
                }

                players.sendMessage(" ");
                players.sendMessage(this.plugin.getPrefix() + "Der Chat wurde von §a" + player.getName() + " §7geleert.");
                players.sendMessage(" ");
            });

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
            return;
        }

    }

}
