package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.apache.commons.validator.routines.InetAddressValidator;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlacklistIpCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public BlacklistIpCommand(Proxy plugin, String name, String permission) {
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
                sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eIP-Adresse §7angeben, um sie zu blacklisten.");
                return;
            } else if(arguments.length == 2) {

                final String ipAdress = arguments[1];

                final InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

                if(!inetAddressValidator.isValidInet4Address(ipAdress)) {
                    sender.sendMessage(this.plugin.getPrefix() + "Die IP-Adresse §e" + arguments[1] + " §7ist nicht gültig.");
                    return;
                }

                if(this.plugin.getBlacklistedIPs().contains(ipAdress)) {
                    sender.sendMessage(this.plugin.getPrefix() + "Die IP-Adresse §e" + ipAdress + " §7ist schon geblacklistet.");
                    return;
                }

                this.plugin.getBlacklistedIPs().add(ipAdress.replaceAll("/", ""));

                final List<String> updatedBlacklist = this.plugin.getConfigManager().getConfiguration().getStringList("Server.Blacklisted-IPs");
                updatedBlacklist.add(ipAdress);

                this.plugin.getConfigManager().getConfiguration().set("Server.Blacklisted-IPs", updatedBlacklist);
                this.plugin.getConfigManager().saveConfig();

                for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                    if(players.hasPermission("proxy.admin") && !this.plugin.getStaffNotifyToggle().contains(players)) {
                        players.sendMessage(this.plugin.getTeamPrefix() + "§7Die IP-Adresse §e" + ipAdress + "§7 wurde geblacklistet.");
                    }
                }

                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Die IP-Adresse §e" + ipAdress + "§7 wurde geblacklistet.");

            } else {
                sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        } else if(arguments[0].equalsIgnoreCase("list")) {

            if(arguments.length != 1) {
                sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
                return;
            }

            sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §eBlacklist §8§m┃--------------------");

            if(this.plugin.getBlacklistedIPs().isEmpty()) {
                sender.sendMessage("§8» §cEs sind momentan keine IP-Adressen auf der §eBlacklist§c.");
            } else {
                for(String ipAdresse : this.plugin.getBlacklistedIPs()) {
                    sender.sendMessage("§8» §e" + ipAdresse);
                }
            }

            sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");

        } else if(arguments[0].equalsIgnoreCase("remove")) {

            final String ipAdress = arguments[1];

            final InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

            if(!inetAddressValidator.isValidInet4Address(ipAdress)) {
                sender.sendMessage(this.plugin.getPrefix() + "Die IP-Adresse §e" + arguments[1] + " §7ist nicht gültig.");
                return;
            }

            if(!this.plugin.getBlacklistedIPs().contains(ipAdress)) {
                sender.sendMessage(this.plugin.getPrefix() + "Die IP-Adresse §e" + ipAdress + " §7ist nicht geblacklistet.");
                return;
            }

            this.plugin.getBlacklistedIPs().remove(ipAdress);

            final List<String> updatedBlacklist = this.plugin.getConfigManager().getConfiguration().getStringList("Server.Blacklisted-IPs");
            updatedBlacklist.remove(ipAdress);

            this.plugin.getConfigManager().getConfiguration().set("Server.Blacklisted-IPs", updatedBlacklist);
            this.plugin.getConfigManager().saveConfig();

            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                if(players.hasPermission("proxy.admin") && !this.plugin.getStaffNotifyToggle().contains(players)) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Die IP-Adresse §e" + ipAdress + "§7 wurde der Blacklistet entfernt.");
                }
            }

            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Die IP-Adresse §e" + ipAdress + "§7 wurde der Blacklist entfernt.");

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAktion §7angeben, die durchgeführt werden soll. (§eadd§7/§elist§7/§eremove§7)");
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(sender.hasPermission("proxy.admin")) {

            if(arguments[0].equalsIgnoreCase("remove")) {
                for(String ipAdress : this.plugin.getBlacklistedIPs()) {
                    output.add(ipAdress);
                }
            }

        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
