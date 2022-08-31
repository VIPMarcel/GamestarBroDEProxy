package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.util.concurrent.TimeUnit;

public record ServerConnectListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onServerConnectEvent(ServerConnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        if(player.getServer() == null) {

            if(player.hasPermission("proxy.staff")) {
                this.plugin.getOnlineStaff().add(player);

                ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
                    if(!(this.plugin.getReports().isEmpty())) {

                        TextComponent prefix = new TextComponent(this.plugin.getTeamPrefix());
                        TextComponent list = new TextComponent("§a§nAlle anzeigen");
                        list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §7Alle §cReports §7anzeigen").create()));
                        list.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports"));
                        prefix.addExtra(list);

                        player.sendMessage(this.plugin.getTeamPrefix() + "§8§m--------------------§r§8┃ §cReport §8§m┃--------------------");
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

                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Unbearbeitete §cReports §8» " + amount);
                        player.sendMessage(prefix);
                        player.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
                    }
                    else {
                        player.sendMessage(this.plugin.getTeamPrefix() + "§7Es liegen momentan keine unbeantworteten §eReports §7vor.");
                    }
                }, 1, TimeUnit.SECONDS);

                ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
                    for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                        if(this.plugin.getOnlineStaff().contains(players)) {
                            players.sendMessage(this.plugin.getTeamPrefix() + "§7Das Teammitglied §c" + player.getName() + " §7ist nun §aonline§7.");
                        }
                    }
                }, 1, TimeUnit.SECONDS);

            }

            if(this.plugin.isMaintenance()) {
                if(!(this.plugin.getWhitelistedUUIDs().contains(player.getUniqueId()) | player.hasPermission("proxy.join.maintenance"))) {
                    event.setCancelled(true);

                    final String maintenanceReason = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Maintenance.Reason"));

                    player.disconnect("\n§8§m--------------------------------------------\n" +
                            "§cDas §6GamestarBro.de Netzwerk §cbefindet sich Wartungsarbeiten.\n\n" +

                            "§7Grund §8» §e" + maintenanceReason + "\n\n" +

                            "§aInformationen und Neuigkeiten findest du auf unserem Discord Server.\n" +
                            "§e§o" + "https://discord.gg/rrtCWGhrrV" + "\n\n" +

                            "§8§m--------------------------------------------\n");
                    return;
                }
            }

            event.setTarget(ProxyServer.getInstance().getServerInfo(this.plugin.getLobbyServerName()));

            final long lastSeenMillis = this.plugin.getDatabasePlayers().getLastSeenMillis(player.getUniqueId());
            final long diffMillis = System.currentTimeMillis() - lastSeenMillis;

            if(diffMillis > TimeUnit.DAYS.toMillis(2)) {
                this.plugin.getDatabasePlayers().setLoginStreak(player.getUniqueId(), 0);
            } else if(TimeUnit.MILLISECONDS.toDays(lastSeenMillis) != TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())) {
                this.plugin.getDatabasePlayers().setLoginStreak(player.getUniqueId(), this.plugin.getDatabasePlayers().getLoginStreak(player.getUniqueId()) + 1);
            }

            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage("§8§m-----------------------------------------------------");
            player.sendMessage("                  §7Herzlich Willkommen auf §6GamestarBro.de");
            player.sendMessage("                         §7Login- Streak §8» §a" + this.plugin.getDatabasePlayers().getLoginStreak(player.getUniqueId()) + "★");
            player.sendMessage("§8§m-----------------------------------------------------");

            if(!this.plugin.getDatabaseVerify().doesPlayerExists(player.getUniqueId())) {
                player.sendMessage(" ");
                player.sendMessage("§8§l┃ §aVerify §8► §7" + "§eDein Account ist noch nicht mit Discord verifiziert.");
                player.sendMessage("§8§l┃ §aVerify §8► §7" + "§7Benutze §e/verify §7um dich zu verifizieren.");
            }

        }

    }

}
