package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.Report;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReportCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public ReportCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {
                player.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um ihn zu §cmelden§7.");
                return;
            }
            final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[0]);

            if(target == null) {
                player.sendMessage(this.plugin.getPrefix() + "§7Der Spieler §e" + arguments[0] + "§7 ist nicht online.");
                return;
            }

            if(arguments.length == 1) {
                player.sendMessage(this.plugin.getPrefix() + "§7Du musst einen §eGrund §7angeben, um §e" + target.getName() + " §7zu melden.");
                return;
            }

            if(target == player) {
                player.sendMessage(this.plugin.getPrefix() + "§cDu darfst dich nicht selbst melden.");
                return;
            }

            if(this.plugin.getReports().containsKey(target)) {
                player.sendMessage(this.plugin.getPrefix() + "§7Der Spieler §e" + target.getName() + "§7 wurde bereits gemeldet.");
                return;
            }

            if(this.plugin.getReportCommandCooldown().contains(player.getUniqueId())) {
                player.sendMessage(this.plugin.getPrefix() + "§7Du darfst nur alle §e5 Sekunden §7einen Spieler melden.");
                return;
            }

            String reason = "";
            for(int i = 1; i < arguments.length; i++) {
                reason = reason + arguments[i] + " ";
            }

            String finalReason = reason;
            this.plugin.getDiscordStaffBOT().sendNewReportMessage(target.getName(), reason, player.getName(), messageId -> {
                Report report = new Report(this.plugin, target, finalReason, player, messageId);
                report.announce();
            });

            player.sendMessage(this.plugin.getPrefix() + "§7Dein Report über §e" + target.getName() + "§7 wurde erstellt!");
            player.sendMessage(this.plugin.getPrefix() + "§cMissbrauch wird bestraft.");

            this.plugin.getReportCommandCooldown().add(player.getUniqueId());

            ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
                this.plugin.getReportCommandCooldown().remove(player.getUniqueId());
            }, 5, TimeUnit.SECONDS);

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDieser Befehl ist nur für echte Spieler geeignet.");
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(arguments.length == 1) {
            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                output.add(players.getName());
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
