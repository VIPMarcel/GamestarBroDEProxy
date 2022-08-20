package vip.marcel.gamestarbro.proxy.commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PardonCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public PardonCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um ihn zu begnadigen.");
            return;
        }

        final UUID uuid = UUIDFetcher.getUUID(arguments[0]);
        final String name = UUIDFetcher.getName(uuid);

        if(uuid == null) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
            return;
        }

        if(arguments.length == 1) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst §eMUTE §7oder §eBANN §7angeben, um §e" + name + " §7zu begnadigen.");
            return;
        }

        final String pardonType = arguments[1].toLowerCase();

        if(pardonType.equals("bann")) {

            if(!this.plugin.getAbuseManager().isAbuse(AbuseType.BAN, uuid)) {
                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist nicht gebannt.");
                return;
            }

            final AbusedInfo abusedInfo = this.plugin.getAbuseManager().getAbuse(AbuseType.BAN, uuid);

            this.plugin.getAbuseManager().deleteAbuse(AbuseType.BAN, uuid);

            String pardonedByName = "";
            if(sender instanceof ProxiedPlayer player) {
                pardonedByName = player.getName();
            } else {
                pardonedByName = "Automatisch | Server";
            }

            String finalPardonedByName = pardonedByName;
            ProxyServer.getInstance().getPlayers().forEach(players -> {
                if(players.hasPermission("proxy.pardon.notify")) {
                    if(!this.plugin.getStaffNotifyToggle().contains(players)) {
                        if(players != sender) {
                            sendPardonStaffMessage(players, name, abusedInfo, "Bann", finalPardonedByName);
                        }
                    }
                }
            });

            sendPardonStaffMessage(sender, name, abusedInfo, "Bann", pardonedByName);

            if(sender != ProxyServer.getInstance().getConsole()) {
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cPardon §8§m┃-------------------");
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + name + " §8┃ §e" + abusedInfo.getAbuseId());
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7AbuseType §8» §e" + "Bann");
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Begnadigt von §8» §e" + pardonedByName);
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
            }

        } else if(pardonType.equals("mute")) {

            if(!this.plugin.getAbuseManager().isAbuse(AbuseType.MUTE, uuid)) {
                sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist nicht gemutet.");
                return;
            }

            final AbusedInfo abusedInfo = this.plugin.getAbuseManager().getAbuse(AbuseType.MUTE, uuid);

            this.plugin.getAbuseManager().deleteAbuse(AbuseType.MUTE, uuid);

            String pardonedByName = "";
            if(sender instanceof ProxiedPlayer player) {
                pardonedByName = player.getName();
            } else {
                pardonedByName = "Automatisch | Server";
            }

            String finalPardonedByName = pardonedByName;
            ProxyServer.getInstance().getPlayers().forEach(players -> {
                if(players.hasPermission("proxy.pardon.notify")) {
                    if(!this.plugin.getStaffNotifyToggle().contains(players)) {
                        if(players != sender) {
                            sendPardonStaffMessage(players, name, abusedInfo, "Mute", finalPardonedByName);
                        }
                    }
                }
            });

            sendPardonStaffMessage(sender, name, abusedInfo, "Mute", pardonedByName);

            if(sender != ProxyServer.getInstance().getConsole()) {
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cPardon §8§m┃-------------------");
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + name + " §8┃ §e" + abusedInfo.getAbuseId());
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7AbuseType §8» §e" + "Mute");
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Begnadigt von §8» §e" + pardonedByName);
                ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst §eMUTE §7oder §eBANN §7angeben, um §e" + name + " §7zu begnadigen.");
        }

    }

    private void sendPardonStaffMessage(CommandSender sender, String name, AbusedInfo abusedInfo, String pardonTypeName, String pardonedByName) {
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cPardon §8§m┃-------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + name + " §8┃ §e" + abusedInfo.getAbuseId());
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7AbuseType §8» §e" + pardonTypeName);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Begnadigt von §8» §e" + pardonedByName);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = Lists.newArrayList();

        if(arguments.length == 1) {
            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                output.add(players.getName());
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
