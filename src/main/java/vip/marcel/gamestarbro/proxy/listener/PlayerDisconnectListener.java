package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;

public record PlayerDisconnectListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        //TODO: remove spam, messagecount, chatcooldown, pm-toggle, msg

        if(this.plugin.getOnlineStaff().contains(player)) {
            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                if(this.plugin.getOnlineStaff().contains(players)) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Das Teammitglied §c" + player.getDisplayName() + " §7ist nun §coffline§7.");
                }
            }
            this.plugin.getOnlineStaff().remove(player);
        }

        if(this.plugin.getStaffNotifyToggle().contains(player)) {
            this.plugin.getStaffNotifyToggle().remove(player);
        }

        //TODO: impl. reports

    }

}
