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

        this.plugin.getChatCooldown().remove(player);
        this.plugin.getPrivateMessageChannel().remove(player);
        this.plugin.getNotifyToggle().remove(player);

        this.plugin.getSocialSpy().remove(player);
        this.plugin.getCommandSpy().remove(player);

        this.plugin.getVerifyCodeCheck().remove(player);
        this.plugin.getVerifyUserCheck().remove(player);

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

        if(this.plugin.getReports().containsKey(player)) {

            final ProxiedPlayer reporter = this.plugin.getReports().get(player).getReporter();
            final ProxiedPlayer reporter2 = ProxyServer.getInstance().getPlayer(reporter.getUniqueId());

            this.plugin.getReports().remove(player);

            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                if(this.plugin.getOnlineStaff().contains(players) && !this.plugin.getStaffNotifyToggle().contains(players)) {
                    players.sendMessage(this.plugin.getTeamPrefix() + "§7Der §cReport §7bezüglich §e" + player.getName() + " §7wurde §cgelöscht§7.");
                }
            }

            if(reporter2 != null) {
                reporter2.sendMessage(this.plugin.getPrefix() + "§7Dein §cReport §7bezüglich §e" + player.getName() + " §7wurde §cgelöscht§7.");
            }

        }

    }

}
