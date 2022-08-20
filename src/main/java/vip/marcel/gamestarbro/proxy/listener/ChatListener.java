package vip.marcel.gamestarbro.proxy.listener;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import java.util.LinkedList;

public record ChatListener(Proxy plugin) implements Listener {

    @EventHandler
    public void onChatEvent(ChatEvent event) {
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if(event.isCommand()) {

            event.setCancelled(true);

            if(event.getMessage().startsWith("/msg ") |
                    event.getMessage().startsWith("/whisper ") |
                    event.getMessage().startsWith("/tell ") |
                    event.getMessage().startsWith("/w ") |
                    event.getMessage().startsWith("/reply ") |
                    event.getMessage().startsWith("/r ")) {

                ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
                    if(this.plugin.getAbuseManager().isAbuse(AbuseType.MUTE, player.getUniqueId())) {
                        handlePlayerMute(player);
                    }
                });

                addToPlayerChatLogs(player, event.getMessage());

            }

            //TODO: Check '/report '

            ProxyServer.getInstance().getPluginManager().dispatchCommand(player, event.getMessage().replaceFirst("/", ""));

        }


    }

    private void addToPlayerChatLogs(ProxiedPlayer player, String message) {
        if(!this.plugin.getSendChatMessages().containsKey(player.getUniqueId())) {
            this.plugin.getSendChatMessages().put(player.getUniqueId(), Lists.newLinkedList());
        }

        final LinkedList<String> messages = this.plugin.getSendChatMessages().get(player.getUniqueId());
        messages.addLast(message);
    }

    private void handlePlayerMute(ProxiedPlayer player) {
        final AbusedInfo abusedInfo = this.plugin.getAbuseManager().getAbuse(AbuseType.MUTE, player.getUniqueId());

        if(abusedInfo.getAbuseExpires() == -1 | (System.currentTimeMillis() - abusedInfo.getAbuseExpires() < 0)) {
            player.sendMessage(" ");
            player.sendMessage(this.plugin.getPrefix() + "§cDu wurdest aus dem §eChat §causgeschlossen.");
            player.sendMessage(this.plugin.getPrefix() + "Grund §8» §e" + abusedInfo.getAbuseReason());
            player.sendMessage(this.plugin.getPrefix() + "AbuseId §8» §e" + abusedInfo.getAbuseId());
            player.sendMessage(this.plugin.getPrefix() + "Dauer §8» §e" + (abusedInfo.getAbuseExpires() == -1 ? "Permanent" : this.plugin.getAbuseTimeManager().getTempBanLength(abusedInfo.getAbuseExpires())));
            //player.sendMessage(this.plugin.getPrefix() + "Von §8» §e" + abusedInfo.getAbusedByName());
            //player.sendMessage("§7Nicht gerechtfertigt? §8» §aEröffne auf unserem Discord Server ein Ticket.");
            player.sendMessage(" ");
            return;
        } else {
            this.plugin.getAbuseManager().deleteAbuse(AbuseType.MUTE, player.getUniqueId());
        }
    }

}
