package vip.marcel.gamestarbro.proxy.listener;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.ClientCommand;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import java.util.LinkedList;

public class ClientCommandPacketListener extends AbstractPacketListener<ClientCommand> {

    private final Proxy plugin;

    public ClientCommandPacketListener(Proxy plugin, Class<ClientCommand> type, Direction direction, int priority) {
        super(type, direction, priority);
        this.plugin = plugin;
    }

    @Override
    public void packetReceive(PacketReceiveEvent<ClientCommand> event) {
        final ClientCommand packet = event.packet();
        final ProtocolizePlayer protocolizePlayer = event.player();
        final ProxiedPlayer player = protocolizePlayer.handle();

        try {
            if(packet.getCommand().startsWith("msg ") |
                    packet.getCommand().startsWith("whisper ") |
                    packet.getCommand().startsWith("tell ") |
                    packet.getCommand().startsWith("w ") |
                    packet.getCommand().startsWith("reply ") |
                    packet.getCommand().startsWith("r ")) {

                // do async?
                if(this.plugin.getAbuseManager().isAbuse(AbuseType.MUTE, player.getUniqueId())) {
                    event.cancelled(true);
                    handlePlayerMute(player);
                } else {
                    addToPlayerChatLogs(player, packet.getCommand());
                    return;
                }

            }
        } catch (UnsupportedOperationException e) {
            ProxyServer.getInstance().getConsole().sendMessage("Packet ClientCommand: " + e.getMessage());
        }

        //TODO: Check reports

    }

    @Override
    public void packetSend(PacketSendEvent<ClientCommand> event) {
    }

    private void addToPlayerChatLogs(ProxiedPlayer player, String message) {
        if(!this.plugin.getSendChatMessages().containsKey(player.getUniqueId())) {
            this.plugin.getSendChatMessages().put(player.getUniqueId(), new LinkedList<>());
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
