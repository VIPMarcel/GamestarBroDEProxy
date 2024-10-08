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

        final String ipAdress = event.getConnection().getSocketAddress().toString().split(":")[0].replaceFirst("/", "");

        if(!this.plugin.getShowServerPings().isEmpty()) {
            final String name = this.plugin.getDatabasePlayers().getPlayerName(ipAdress);

            this.plugin.getShowServerPings().forEach(player -> {
                if(name != null) {
                    player.sendMessage("§8§l┃ §6Serverliste §8► §7" + "§e" + ipAdress + "§8(§e" + name + "§8) §7hat gepingt");
                } else {
                    player.sendMessage("§8§l┃ §6Serverliste §8► §7" + "§e" + ipAdress + " §7hat gepingt.");
                }
            });
        }

        if(this.plugin.getBlacklistedIPs().contains(ipAdress)) {
            ping.setVersion(new ServerPing.Protocol("", Short.MAX_VALUE));
            ping.setDescription("§4Can't connect to server");
            return;
        }

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
