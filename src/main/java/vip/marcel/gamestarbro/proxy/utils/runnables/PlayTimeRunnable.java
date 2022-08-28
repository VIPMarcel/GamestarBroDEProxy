package vip.marcel.gamestarbro.proxy.utils.runnables;

import net.md_5.bungee.api.ProxyServer;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.util.concurrent.TimeUnit;

public record PlayTimeRunnable(Proxy plugin) {

    public void run() {
        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
            ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
                ProxyServer.getInstance().getPlayers().forEach(players -> {
                    this.plugin.getDatabasePlayers().setPlayTime(players.getUniqueId(), this.plugin.getDatabasePlayers().getPlayTime(players.getUniqueId()) + 1);
                });
            }, 1, 1, TimeUnit.SECONDS);
        });
    }

}
