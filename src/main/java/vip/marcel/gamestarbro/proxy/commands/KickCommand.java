package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class KickCommand extends Command {

    private final Proxy plugin;

    public KickCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um ihn zu kicken.");
            return;
        }
        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[0]);

        if(target == null) {
            sender.sendMessage(this.plugin.getPrefix() + "§cDer Spieler §e" + arguments[0] + "§c ist nicht online.");
            return;
        }

        if(target == sender) {
            sender.sendMessage(this.plugin.getPrefix() + "§cDu darfst dich nicht selbst kicken.");
            return;
        }

        if(target.hasPermission("proxy.abuse.bypass")) {
            sender.sendMessage(this.plugin.getPrefix() + "§cDu darfst keine §eTeammitglieder §ckicken.");
            return;
        }

        String reason = "";

        if(arguments.length == 1) {
            reason = "Verwarnung";
        } else {
            for(int i = 1; i < arguments.length; i++) {
                reason = reason + arguments[i] + " ";
            }
        }

        reason = ChatColor.translateAlternateColorCodes('&', reason);

        String kickedByName = "";
        if(sender instanceof ProxiedPlayer player) {
            kickedByName = player.getName();
        } else {
            kickedByName = "Automatisch | Server";
        }

        target.disconnect("\n§8§m--------------------------------------------\n" +
                "§cDu wurdest vom §6GamestarBro.de Netzwerk §cgeworfen.\n\n" +

                "§7Grund §8» §e" + reason + "\n" +
                /*"§7Von §8» §e" + abusedInfo.getAbusedByName() + "\n*/"\n\n" +

                "§7Nicht gerechtfertigt? §8» §aEröffne auf unserem Discord Server ein Ticket.\n" +
                "§e§o" + "https://discord.gg/rrtCWGhrrV" + "\n\n" +

                "§8§m--------------------------------------------\n");

        String finalReason = reason;
        String finalKickedByName = kickedByName;
        ProxyServer.getInstance().getPlayers().forEach(players -> {
            if(players.hasPermission("proxy.abuse.notify")) {
                if(!this.plugin.getStaffNotifyToggle().contains(players)) {
                    if(players != sender) {
                        sendStaffKickedMessage(sender, target, finalReason, finalKickedByName);
                    }
                }
            }
        });

        sendStaffKickedMessage(sender, target, reason, kickedByName);

        if(sender != ProxyServer.getInstance().getConsole()) {
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m----------------------§r§8┃ §cKick §8§m┃---------------------");
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» " + target.getName());
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Grund §8» §e" + reason);
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Gekickt von §8» §e" + kickedByName);
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
        }

    }

    private void sendStaffKickedMessage(CommandSender sender, ProxiedPlayer target, String reason, String kickedByName) {
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m----------------------§r§8┃ §cKick §8§m┃---------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» " + target.getName());
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Grund §8» §e" + reason);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Gekickt von §8» §e" + kickedByName);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
    }

}
