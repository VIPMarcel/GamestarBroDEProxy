package vip.marcel.gamestarbro.proxy.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import java.util.UUID;

public record LoginListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onLoginEvent(LoginEvent event) {
        final UUID uuid = event.getConnection().getUniqueId();

        event.registerIntent(this.plugin);

        if(this.plugin.getBlacklistedUUIDs().contains(uuid)) {
            event.setCancelled(true);
            event.setCancelReason("io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information:");
            event.completeIntent(this.plugin);
            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {

            if(this.plugin.getAbuseManager().isAbuse(AbuseType.BAN, uuid)) {
                final AbusedInfo abusedInfo = this.plugin.getAbuseManager().getAbuse(AbuseType.BAN, uuid);

                if(abusedInfo.getAbuseExpires() == -1 | (System.currentTimeMillis() - abusedInfo.getAbuseExpires() < 0)) {
                    event.setCancelled(true);
                    event.setCancelReason("\n§8§m--------------------------------------------\n" +
                            "§cDu wurdest vom §6GamestarBro.de Netzwerk §causgeschlossen.\n\n" +

                            "§7Grund §8» §e" + abusedInfo.getAbuseReason() + "\n" +
                            "§7AbuseId §8» §e" + abusedInfo.getAbuseId() + "\n\n" +

                            "§7Dauer §8» §e" + (abusedInfo.getAbuseExpires() == -1 ? "Permanent" : this.plugin.getAbuseTimeManager().getTempBanLength(abusedInfo.getAbuseExpires())) + "\n" +
                            /*"§7Von §8» §e" + abusedInfo.getAbusedByName() + "\n*/"\n\n" +

                            "§7Nicht gerechtfertigt? §8» §aEröffne auf unserem Discord Server ein Ticket.\n" +
                            "§e§o" + "https://discord.gg/rrtCWGhrrV" + "\n\n" +

                            "§8§m--------------------------------------------\n");
                } else {
                    this.plugin.getAbuseManager().deleteAbuse(AbuseType.BAN, uuid);
                }
            }

            this.plugin.getDatabasePlayers().createPlayer(uuid);
            this.plugin.getDatabasePlayers().setIPAdress(uuid, event.getConnection().getSocketAddress().toString().split(":")[0]);

            if(ProxyServer.getInstance().getPlayers().size() + this.plugin.getFakePlayers() >= this.plugin.getServerSlots()) {
                if(!this.plugin.hasPermission(uuid, "proxy.join.full")) {
                    event.setCancelled(true);
                    event.setCancelReason("\n§8§m--------------------------------------------\n" +
                            "§cDas §6GamestarBro.de Netzwerk §chat seine §emaximale Spieleranzahl §cerreicht.\n\n" +

                            "§cDu benötigst einen höheren Rang um noch beitreten zu dürfen.\n" +
                            "§e§o" + "https://discord.gg/rrtCWGhrrV" + "\n\n" +

                            "§8§m--------------------------------------------\n");
                }
            }

            event.completeIntent(this.plugin);

        });

    }

}
