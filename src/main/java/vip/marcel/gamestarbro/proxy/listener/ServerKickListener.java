package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;

public record ServerKickListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onServerKickEvent(ServerKickEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        if(player.getServer() != null) {

            if(ProxyServer.getInstance().getServerInfo(player.getServer().getInfo().getName()) != ProxyServer.getInstance().getServerInfo(this.plugin.getLobbyServerName())) {
                event.setCancelled(true);
                player.sendMessage(event.getKickReason());
            }

        }

    }

}
