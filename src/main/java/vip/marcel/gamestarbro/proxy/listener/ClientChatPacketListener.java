package vip.marcel.gamestarbro.proxy.listener;

import com.google.common.collect.Lists;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.ClientChat;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import java.util.LinkedList;

public class ClientChatPacketListener extends AbstractPacketListener<ClientChat> {

    private final Proxy plugin;

    public ClientChatPacketListener(Proxy plugin, Class<ClientChat> type, Direction direction, int priority) {
        super(type, direction, priority);
        this.plugin = plugin;
    }

    @Override
    public void packetReceive(PacketReceiveEvent<ClientChat> event) {
        final ClientChat packet = event.packet();
        final ProtocolizePlayer protocolizePlayer = event.player();
        final ProxiedPlayer player = protocolizePlayer.handle();

        if(packet.getMessage().startsWith("/")) {
            return;
        }

        event.cancelled(true);

        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
            if(this.plugin.getAbuseManager().isAbuse(AbuseType.MUTE, player.getUniqueId())) {
                handlePlayerMute(player);
            } else {

                //TODO: Check spam, blackwords, server-werbung

                addToPlayerChatLogs(player, packet.getMessage());
                protocolizePlayer.sendPacketToServer(packet);
            }
        });

    }

    @Override
    public void packetSend(PacketSendEvent<ClientChat> event) {
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
