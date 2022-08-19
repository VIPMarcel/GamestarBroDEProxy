package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.UUID;

public class PardonIdCommand extends Command {

    private final Proxy plugin;

    public PardonIdCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAbuseId §7angeben, um sie zu begnadigen.");
            return;
        }

        final String abuseId = arguments[0];
        AbuseType abuseType = null;

        if(this.plugin.getAbuseManager().isAbuseIdInUseBans(abuseId)) {
            abuseType = AbuseType.BAN;
        } else if(this.plugin.getAbuseManager().isAbuseIdInUseBans(abuseId)) {
            abuseType = AbuseType.MUTE;
        } else {
            sender.sendMessage(this.plugin.getPrefix() + "Die AbuseId §e" + arguments[0] + " §7existiert nicht.");
            return;
        }

        if(abuseType.equals(AbuseType.BAN)) {

            if(!this.plugin.getAbuseManager().isAbuseIdInUseBans(abuseId)) {
                sender.sendMessage(this.plugin.getPrefix() + "Die AbuseId §e" + abuseId + " §7ist nicht gebannt.");
                return;
            }

            final AbusedInfo abusedInfo = this.plugin.getBanAbuse().getAbuse(abuseId);

            final UUID uuid = UUID.fromString(abusedInfo.getUuid().toString());
            final String name = UUIDFetcher.getName(uuid);

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

        } else if(abuseType.equals(AbuseType.MUTE)) {

            if(!this.plugin.getAbuseManager().isAbuseIdInUseMutes(abuseId)) {
                sender.sendMessage(this.plugin.getPrefix() + "Die AbuseId §e" + abuseId + " §7ist nicht gemutet.");
                return;
            }

            final AbusedInfo abusedInfo = this.plugin.getMuteAbuse().getAbuse(abuseId);

            final UUID uuid = UUID.fromString(abusedInfo.getUuid().toString());
            final String name = UUIDFetcher.getName(uuid);

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

        }

    }

    private void sendPardonStaffMessage(CommandSender sender, String name, AbusedInfo abusedInfo, String pardonTypeName, String pardonedByName) {
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cPardon §8§m┃-------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + name + " §8┃ §e" + abusedInfo.getAbuseId());
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7AbuseType §8» §e" + pardonTypeName);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Begnadigt von §8» §e" + pardonedByName);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
    }

}
