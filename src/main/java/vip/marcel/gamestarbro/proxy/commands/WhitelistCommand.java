package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class WhitelistCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public WhitelistCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAktion §7angeben, die durchgeführt werden soll. (§eadd§7/§elist§7/§eremove§7)");
            return;
        }

        if(arguments[0].equalsIgnoreCase("add")) {

            if(arguments.length == 1) {
                sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um ihn zu whitelisten.");
                return;
            } else if(arguments.length == 2) {

                final UUID uuid = UUIDFetcher.getUUID(arguments[1]);
                final String name = UUIDFetcher.getName(uuid);

                if(uuid == null) {
                    sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[1] + " §7existiert nicht.");
                    return;
                }

                if(this.plugin.getWhitelistedUUIDs().contains(uuid)) {
                    sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist schon gewhitelistet.");
                    return;
                }

                this.plugin.getWhitelistedUUIDs().add(uuid);

                final List<String> updatedWhitelist = this.plugin.getConfigManager().getConfiguration().getStringList("Server.Maintenance.Allowed-UUIDs");
                updatedWhitelist.add(uuid.toString());

                this.plugin.getConfigManager().getConfiguration().set("Server.Maintenance.Allowed-UUIDs", updatedWhitelist);
                this.plugin.getConfigManager().saveConfig();

                if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(this.plugin.getPrefix() + "§eDu darfst das Netzwerk nun auch während Wartungsarbeiten betreten.");
                }

                for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                    if(players.hasPermission("proxy.admin") && !this.plugin.getStaffNotifyToggle().contains(players)) {
                        players.sendMessage(this.plugin.getTeamPrefix() + "§7Der Spieler §e" + name + "§7 wurde gewhitelistet.");
                    }
                }

                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Der Spieler §e" + name + "§7 wurde gewhitelistet.");

            } else {
                sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        } else if(arguments[0].equalsIgnoreCase("list")) {

            if(arguments.length != 1) {
                sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §eBlacklist §8§m┃--------------------");

            if(this.plugin.getWhitelistedUUIDs().isEmpty()) {
                sender.sendMessage("§8» §cEs sind momentan keine Spieler auf der §eWhitelist§c.");
            } else {
                for(UUID uuid : this.plugin.getWhitelistedUUIDs()) {
                    sender.sendMessage("§8» §e" + UUIDFetcher.getName(uuid));
                }
            }

            sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");

        } else if(arguments[0].equalsIgnoreCase("remove")) {

            final UUID uuid = UUIDFetcher.getUUID(arguments[1]);
            final String name = UUIDFetcher.getName(uuid);

            if(uuid == null) {
                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[1] + " §7existiert nicht.");
                return;
            }

            if(!this.plugin.getWhitelistedUUIDs().contains(uuid)) {
                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist nicht gewhitelistet.");
                return;
            }

            this.plugin.getWhitelistedUUIDs().remove(uuid);

            final List<String> updatedWhitelist = this.plugin.getConfigManager().getConfiguration().getStringList("Server.Maintenance.Allowed-UUIDs");
            updatedWhitelist.remove(uuid.toString());

            this.plugin.getConfigManager().getConfiguration().set("Server.Maintenance.Allowed-UUIDs", updatedWhitelist);
            this.plugin.getConfigManager().saveConfig();

            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                if(players.hasPermission("proxy.admin") && !this.plugin.getStaffNotifyToggle().contains(players)) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Der Spieler §e" + name + "§7 wurde der Whitelist entfernt.");
                }
            }

            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Der Spieler §e" + name + "§7 wurde der Whitelist entfernt.");

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAktion §7angeben, die durchgeführt werden soll. (§eadd§7/§elist§7/§eremove§7)");
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(sender.hasPermission("proxy.admin")) {

            if(arguments[0].equalsIgnoreCase("add")) {
                for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                    output.add(players.getName());
                }
            }

            if(arguments[0].equalsIgnoreCase("remove")) {
                for(UUID uuid : this.plugin.getWhitelistedUUIDs()) {
                    output.add(UUIDFetcher.getName(uuid));
                }
            }

        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
