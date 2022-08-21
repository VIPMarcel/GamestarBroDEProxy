package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
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

                //TODO: List up new reports

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

            //TODO: impl. loginstreak

            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage("§8§m-----------------------------------------------------");
            player.sendMessage("                  §7Herzlich Willkommen auf §6GamestarBro.de");
            player.sendMessage("                         §7Login- Streak §8» §a100★");
            player.sendMessage("§8§m-----------------------------------------------------");

        }

    }

}
