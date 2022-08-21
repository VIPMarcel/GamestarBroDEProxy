package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public record ProxyPingListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onProxyPingEvent(ProxyPingEvent event) {
        final ServerPing ping = event.getResponse();
        final ServerPing.Players players = ping.getPlayers();

        final String motd1 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.1"));
        final String motd2 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.2"));
        final String motdMaintenance = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.Maintenance"));

        if(this.plugin.isMaintenance()) {
            ping.setVersion(new ServerPing.Protocol("§4✘ §8┃ §cWartungsarbeiten", Short.MAX_VALUE));
            ping.setDescription(motd1 + "\n" + motdMaintenance);
        } else {
            players.setMax(this.plugin.getServerSlots());
            players.setOnline(ProxyServer.getInstance().getOnlineCount() + this.plugin.getFakePlayers());
            //ping.setVersion(new ServerPing.Protocol("§2✓ §8┃ §aOpen Beta", Short.MAX_VALUE));
            ping.setDescription(motd1 + "\n" + motd2);
        }

        try {
            ping.setFavicon(Favicon.create(ImageIO.read(new File(this.plugin.getDataFolder().getPath(), "server-icon.png"))));
        } catch(IOException e) {
            e.printStackTrace();
        }

        ping.setPlayers(players);

    }

}
