package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class MaintenanceCommand extends Command {

    private final Proxy plugin;

    public MaintenanceCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        final String motd1 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.1"));
        final String motd2 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.2"));
        final String motdMaintenance = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.Maintenance"));

        if(this.plugin.isMaintenance()) {

            if(arguments.length != 0) {
                sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            this.plugin.setMaintenance(false);
            this.plugin.getConfigManager().getConfiguration().set("Server.Maintenance.State", false);
            this.plugin.getConfigManager().getConfiguration().set("Server.Maintenance.Reason", "");
            this.plugin.getConfigManager().saveConfig();

            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                players.sendMessage(this.plugin.getPrefix() + "§eDie Wartungsarbeiten wurden §cdeaktiviert§e.");
                players.setTabHeader(new TextComponent(motd1 + "\n" + motd2 + "\n\n§7Partner §8➠ §bTube-Hosting.de\n"), new TextComponent("\n§7Server §8» §a" + players.getServer().getInfo().getName() + "\n\n§7Benutze §c/report §7wenn du einen §eRegelbrecher siehst.\n"));
            }
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getPrefix() + "§eDie Wartungsarbeiten wurden §cdeaktiviert§e.");

        } else {

            if(arguments.length == 0) {
                sender.sendMessage(this.plugin.getPrefix() + "§cDu musst einen §eGrund §cangeben, um die Wartungsarbeiten zu aktivieren.");
                return;
            }

            String reason = "";
            for(int i = 0; i < arguments.length; i++) {
                reason = reason + arguments[i] + " ";
            }

            this.plugin.setMaintenance(true);
            this.plugin.getConfigManager().getConfiguration().set("Server.Maintenance.State", true);
            this.plugin.getConfigManager().getConfiguration().set("Server.Maintenance.Reason", reason);
            this.plugin.getConfigManager().saveConfig();

            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                if(!(this.plugin.getWhitelistedUUIDs().contains(players.getUniqueId()) | players.hasPermission("proxy.join.maintenance"))) {
                    players.disconnect("\n§8§m--------------------------------------------\n" +
                            "§cDas §6GamestarBro.de Netzwerk §cbefindet sich Wartungsarbeiten.\n\n" +

                            "§7Grund §8» §e" + ChatColor.translateAlternateColorCodes('&', reason) + "\n\n" +

                            "§aInformationen und Neuigkeiten findest du auf unserem Discord Server.\n" +
                            "§e§o" + "https://discord.gg/rrtCWGhrrV" + "\n\n" +

                            "§8§m--------------------------------------------\n");
                }

                players.sendMessage(this.plugin.getPrefix() + "§eDie Wartungsarbeiten wurden §aaktiviert§e.");
                players.sendMessage(this.plugin.getPrefix() + " §8➟ §e" + ChatColor.translateAlternateColorCodes('&', reason));
                players.setTabHeader(new TextComponent(motd1 + "\n" + motdMaintenance + "\n\n§7Partner §8➠ §bTube-Hosting.de\n"), new TextComponent("\n§7Server §8» §a" + players.getServer().getInfo().getName() + "\n\n§7Benutze §c/report §7wenn du einen §eRegelbrecher siehst.\n"));
            }

            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getPrefix() + "§eDie Wartungsarbeiten wurden §aaktiviert§e.");
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getPrefix() + " §8➟ §e" + ChatColor.translateAlternateColorCodes('&', reason));

        }

    }

}
