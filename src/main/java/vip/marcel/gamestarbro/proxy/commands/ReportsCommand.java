package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.Report;

import java.awt.*;
import java.util.Map;

public class ReportsCommand extends Command {

    private final Proxy plugin;

    public ReportsCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {

                if(this.plugin.getReports().size() == 0) {
                    player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegen momentan keine unbeantworteten §eReports §7vor.");
                    return;
                }

                player.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cReport §8§m┃--------------------");
                int count = 0;

                for(Map.Entry<ProxiedPlayer, Report> entry : this.plugin.getReports().entrySet()) {
                    count++;

                    TextComponent tc = new TextComponent(this.plugin.getTeamPrefix() + "§e" + count + ". §8» §e" + entry.getKey().getName() + " §8┃ ");

                    TextComponent list = new TextComponent("§aAnzeigen");
                    list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §cReport §7anzeigen").create()));
                    list.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports info " + entry.getKey().getName()));

                    tc.addExtra(list);

                    player.sendMessage(tc);
                }
                player.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
                return;

            }
            else if(arguments.length == 2) {

                if(arguments[0].equalsIgnoreCase("info")) {
                    final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[1]);

                    if(target == null) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + arguments[1] + " §7vor.");
                        return;
                    }

                    if(!(this.plugin.getReports().containsKey(target))) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + target.getName() + "§7 vor.");
                        return;
                    }

                    Report report = this.plugin.getReports().get(target);

                    report.sendDetails(player);
                    return;
                }

                if(arguments[0].equalsIgnoreCase("accept")) {
                    final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[1]);

                    if(target == null) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + arguments[1] + " §7vor.");
                        return;
                    }

                    if(!(this.plugin.getReports().containsKey(target))) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + target.getName() + "§7 vor.");
                        return;
                    }

                    Report report = this.plugin.getReports().get(target);

                    report.acceptReport(player);
                    this.plugin.getDiscordStaffBOT().updateReportMessage(report.getDiscordMessageId(), Color.GREEN, report.getReported().getName(), report.getReason(), report.getReporter().getName(), player.getName());
                    return;
                }

                if(arguments[0].equalsIgnoreCase("deny")) {
                    final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[1]);

                    if(target == null) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + arguments[1] + " §7vor.");
                        return;
                    }

                    if(!(this.plugin.getReports().containsKey(target))) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + target.getName() + "§7 vor.");
                        return;
                    }

                    Report report = this.plugin.getReports().get(target);

                    report.denyReport(player);
                    this.plugin.getDiscordStaffBOT().updateReportMessage(report.getDiscordMessageId(), Color.RED, report.getReported().getName(), report.getReason(), report.getReporter().getName(), player.getName());
                    return;
                }

                if(arguments[0].equalsIgnoreCase("ban")) {
                    final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[1]);

                    if(target == null) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + arguments[1] + " §7vor.");
                        return;
                    }

                    if(!(this.plugin.getReports().containsKey(target))) {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegt kein §eReport §7bezüglich des Spielers §e" + target.getName() + "§7 vor.");
                        return;
                    }

                    Report report = this.plugin.getReports().get(target);

                    report.banReport(player);
                    this.plugin.getDiscordStaffBOT().updateReportMessage(report.getDiscordMessageId(), Color.RED, report.getReported().getName(), report.getReason(), report.getReporter().getName(), player.getName());
                    return;
                }

            }
            else {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDer Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
