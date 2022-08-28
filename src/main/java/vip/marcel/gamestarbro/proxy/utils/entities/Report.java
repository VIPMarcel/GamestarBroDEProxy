package vip.marcel.gamestarbro.proxy.utils.entities;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.UUID;

public class Report {

    private final Proxy plugin;

    private ProxiedPlayer reported, reporter;
    private String reason, discordMessageId;

    public Report(Proxy plugin, ProxiedPlayer reported, String reason, ProxiedPlayer reporter, String discordMessageId) {
        this.plugin = plugin;

        this.reported = reported;
        this.reason = reason;
        this.reporter = reporter;
        this.discordMessageId = discordMessageId;

        this.plugin.getReports().put(reported, this);
    }

    public void announce() {
        TextComponent prefix = new TextComponent(this.plugin.getTeamPrefix());
        TextComponent list = new TextComponent("§a§nAlle anzeigen");
        list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §7Alle §cReports §7anzeigen").create()));
        list.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports"));
        prefix.addExtra(list);

        for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
            if(this.plugin.getOnlineStaff().contains(players) && !this.plugin.getStaffNotifyToggle().contains(players)) {

                players.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cReport §8§m┃--------------------");
                players.sendMessage(this.plugin.getTeamPrefix() + "§7Ein neuer §cReport §7ist eingegangen.");

                boolean selected = false;
                String amount = "";

                if(this.plugin.getReports().size() < 4) {
                    amount = "§a" + this.plugin.getReports().size();
                    selected = true;
                }

                if(this.plugin.getReports().size() < 7 && !selected) {
                    amount = "§e" + this.plugin.getReports().size();
                    selected = true;
                }

                if(this.plugin.getReports().size() < 10 && !selected) {
                    amount = "§c" + this.plugin.getReports().size();
                    selected = true;
                }

                if(this.plugin.getReports().size() < 13 && !selected) {
                    amount = "§4" + this.plugin.getReports().size();
                    selected = true;
                }

                if(this.plugin.getReports().size() >= 13 && !selected) {
                    amount = "§4§l" + this.plugin.getReports().size();
                    selected = true;
                }

                players.sendMessage(this.plugin.getTeamPrefix() + "§7Unbearbeitete §cReports §8» " + amount);
                players.sendMessage(prefix);
                players.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");

            }
        }
    }

    public void sendDetails(ProxiedPlayer player) {
        TextComponent tc = new TextComponent(this.plugin.getTeamPrefix() + "§7Server §8» ");
        TextComponent server = new TextComponent("§e" + this.reported.getServer().getInfo().getName());
        server.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §aZu ihm springen").create()));
        server.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + this.reported.getServer().getInfo().getName()));
        tc.addExtra(server);

        TextComponent action = new TextComponent(this.plugin.getTeamPrefix());

        TextComponent accept = new TextComponent("§aAnnehmen");
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §aAnnehmen").create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports accept " + this.reported.getName()));

        TextComponent spacer = new TextComponent(" §8┃ ");

        TextComponent deny = new TextComponent("§cAblehnen");
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §cAblehnen").create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports deny " + this.reported.getName()));

        TextComponent ban = new TextComponent("§eUnnötiger Report");
        ban.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §eUnnötiger Report").create()));
        ban.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports ban " + this.reported.getName()));

        action.addExtra(accept);
        action.addExtra(spacer);
        action.addExtra(deny);
        action.addExtra(spacer);
        action.addExtra(ban);

        player.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cReport §8§m┃--------------------");
        player.sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + this.reported.getName());
        player.sendMessage(this.plugin.getTeamPrefix() + "§7Grund §8» §e" + this.reason);
        player.sendMessage(this.plugin.getTeamPrefix() + "§7Reportet von §8» §e" + this.reporter.getName());
        player.sendMessage(tc);
        player.sendMessage(this.plugin.getTeamPrefix());
        player.sendMessage(action);
        player.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
    }

    public void acceptReport(ProxiedPlayer player) {
        this.plugin.getReports().remove(this.reported);

        player.sendMessage(this.plugin.getTeamPrefix() + "Du hast den §cReport §7bezüglich §e" + this.reported.getName() + " §aangenommen§7.");

        if(player.getServer().getInfo() != this.reported.getServer().getInfo()) {
            player.connect(this.reported.getServer().getInfo());
        }

        for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
            if(this.plugin.getOnlineStaff().contains(players) && !this.plugin.getStaffNotifyToggle().contains(players)) {
                if(players != player) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Der §cReport §7bezüglich §e" + this.reported.getName() + " §7wurde von §c" + player.getName() + " §aangenommen§7.");
                }
            }
        }

        final ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(this.reporter.getUniqueId());

        if(reporter != null) {
            reporter.sendMessage(this.plugin.getPrefix() + "§7Dein §cReport §7bezüglich §e" + this.reported.getName() + " §7wurde von §c" + player.getName() + " §aangenommen§7.");
        }
    }

    public void denyReport(ProxiedPlayer player) {
        this.plugin.getReports().remove(this.reported);

        player.sendMessage(this.plugin.getTeamPrefix() + "Du hast den §cReport §7bezüglich §e" + this.reported.getName() + " §cabgelehnt§7.");

        for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
            if(this.plugin.getOnlineStaff().contains(players) && !this.plugin.getStaffNotifyToggle().contains(players)) {
                if(players != player) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Der §cReport §7bezüglich §e" + this.reported.getName() + " §7wurde von §c" + player.getName() + " §cabgelehnt§7.");
                }
            }
        }

        final ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(this.reporter.getUniqueId());

        if(reporter != null) {
            reporter.sendMessage(this.plugin.getPrefix() + "§7Dein §cReport §7bezüglich §e" + this.reported.getName() + " §7wurde von §c" + player.getName() + " §cabgelehnt§7.");
        }

    }

    public void banReport(ProxiedPlayer player) {
        this.plugin.getReports().remove(this.reported);

        player.sendMessage(this.plugin.getTeamPrefix() + "Du hast den §cReport §7bezüglich §e" + this.reported.getName() + " §cabgelehnt§7.");

        for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
            if(this.plugin.getOnlineStaff().contains(players) && !this.plugin.getStaffNotifyToggle().contains(players)) {
                if(players != player) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Der §cReport §7bezüglich §e" + this.reported.getName() + " §7wurde von §c" + player.getName() + " §cabgelehnt§7.");
                }
            }
        }

        final ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(this.reporter.getUniqueId());
        final UUID uuid = UUIDFetcher.getUUID(this.reporter.getName());
        final String name = UUIDFetcher.getName(uuid);

        if(this.plugin.hasPermission(uuid, "proxy.abuse.bypass")) {
            player.sendMessage(this.plugin.getPrefix() + "§cDu darfst keine §eTeammitglieder §cbestrafen.");
            return;
        }

        ProxyServer.getInstance().getPluginManager().dispatchCommand(player, "abuse " + name + " 10");

    }

    public ProxiedPlayer getReported() {
        return this.reported;
    }

    public ProxiedPlayer getReporter() {
        return this.reporter;
    }

    public String getReason() {
        return this.reason;
    }

    public String getDiscordMessageId() {
        return this.discordMessageId;
    }

}
