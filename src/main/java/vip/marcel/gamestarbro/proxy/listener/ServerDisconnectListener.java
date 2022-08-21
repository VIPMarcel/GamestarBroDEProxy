package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;

public record ServerDisconnectListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onServerDisconnectEvent(ServerDisconnectEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
           this.plugin.getDatabasePlayers().setLastSeenMillis(player.getUniqueId(), System.currentTimeMillis());
        });

    }

}
