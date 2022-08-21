package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;

public record ServerConnectedListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onServerConnectedEvent(ServerConnectedEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        if(this.plugin.getJoinMe().containsKey(player)) {
            this.plugin.getJoinMe().remove(player);
        }

        final String motd1 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.1"));
        final String motd2 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.2"));
        final String motdMaintenance = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.Maintenance"));

        if(this.plugin.isMaintenance()) {
            player.setTabHeader(new TextComponent(motd1 + "\n" + motdMaintenance + "\n\n§7Partner §8➠ §bTube-Hosting.de\n"), new TextComponent("\n§7Server §8» §a" + event.getServer().getInfo().getName() + "\n\n§7Benutze §c/report §7wenn du einen §eRegelbrecher siehst.\n"));
        } else {
            player.setTabHeader(new TextComponent(motd1 + "\n" + motd2 + "\n\n§7Partner §8➠ §bTube-Hosting.de\n"), new TextComponent("\n§7Server §8» §a" + event.getServer().getInfo().getName() + "\n\n§7Benutze §c/report §7wenn du einen §eRegelbrecher siehst.\n"));
        }

    }

}
