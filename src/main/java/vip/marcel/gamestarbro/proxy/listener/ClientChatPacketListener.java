package vip.marcel.gamestarbro.proxy.listener;

import com.google.common.collect.Lists;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.ClientChat;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

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

            if(this.plugin.getVerifyCodeCheck().containsKey(player)) {
                if(packet.getMessage().equals(this.plugin.getVerifyCodeCheck().get(player))) {
                    final User user = this.plugin.getVerifyUserCheck().get(player);
                    final String userName = user.getName();

                    this.plugin.getDatabaseVerify().createPlayer(player.getUniqueId(), user.getId());

                    this.plugin.getDiscordStaffBOT().addVerifiedRole(user);

                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "§aDu hast dich erfolgreich verifiziert mit §e" + userName + "§a.");
                    player.sendMessage(this.plugin.getPrefix() + "Dir wurden §e5.000 Coins §7gutgeschrieben.");
                    this.plugin.getDatabasePlayers().setCoins(player.getUniqueId(), this.plugin.getDatabasePlayers().getCoins(player.getUniqueId()) + 5000);
                } else {
                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "§cDu hast einen falschen §eVerifizierungscode §ceingegeben.");
                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "§cGib erneut §e/verify §cein, um dich zu verifizieren.");
                }
                this.plugin.getVerifyCodeCheck().remove(player);
                this.plugin.getVerifyUserCheck().remove(player);
                return;
            }

            if(this.plugin.getAbuseManager().isAbuse(AbuseType.MUTE, player.getUniqueId())) {
                handlePlayerMute(player);
            } else {

                if(!player.hasPermission("proxy.admin")) {

                    if(!this.plugin.getSpamCount().containsKey(player.getUniqueId())) {
                        this.plugin.getSpamCount().put(player.getUniqueId(), 0);
                    }

                    if(!this.plugin.getChatCooldown().contains(player.getUniqueId())) {
                        this.plugin.getChatCooldown().add(player.getUniqueId());

                        ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
                            this.plugin.getChatCooldown().remove(player.getUniqueId());
                        }, 1, TimeUnit.SECONDS);

                    } else {
                        this.plugin.getSpamCount().put(player.getUniqueId(), this.plugin.getSpamCount().get(player.getUniqueId()) + 1);

                        ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {
                            this.plugin.getSpamCount().remove(player.getUniqueId());
                        }, 5, TimeUnit.SECONDS);

                        player.sendMessage(this.plugin.getPrefix() + "§cBitte schreibe etwas langsamer.");

                        if(this.plugin.getSpamCount().get(player.getUniqueId()).equals(3)) {
                            this.plugin.getSpamCount().remove(player.getUniqueId());

                            if(!this.plugin.getMessageCount().containsKey(player.getUniqueId())) {
                                this.plugin.getMessageCount().put(player.getUniqueId(), 1);
                            } else {
                                this.plugin.getMessageCount().put(player.getUniqueId(), this.plugin.getMessageCount().get(player.getUniqueId()) + 1);
                            }

                            if(this.plugin.getMessageCount().get(player.getUniqueId()).equals(1)) {
                                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "kick " + player.getName() + " Spam");
                            } else {
                                this.plugin.getMessageCount().remove(player.getUniqueId());
                                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "abuse " + player.getName() + " 6");
                            }

                            return;
                        }
                    }

                    if(this.plugin.getChatFilter().messageContainsBlackListedWords(packet.getMessage())) {

                        if(!this.plugin.getInsultingCount().containsKey(player.getUniqueId())) {
                            this.plugin.getInsultingCount().put(player.getUniqueId(), 1);
                        }

                        if(this.plugin.getInsultingCount().get(player.getUniqueId()).equals(0) |
                                this.plugin.getInsultingCount().get(player.getUniqueId()).equals(1) |
                                this.plugin.getInsultingCount().get(player.getUniqueId()).equals(2)) {

                            addToPlayerChatLogs(player, packet.getMessage());
                            player.sendMessage(this.plugin.getPrefix() + "§cBitte achte auf deine Wortwahl.");
                            this.plugin.getInsultingCount().put(player.getUniqueId(), this.plugin.getInsultingCount().get(player.getUniqueId()) + 1);

                            return;
                        }

                        if(this.plugin.getInsultingCount().get(player.getUniqueId()).equals(3)) {
                            this.plugin.getInsultingCount().remove(player.getUniqueId());

                            addToPlayerChatLogs(player, packet.getMessage());
                            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "abuse " + player.getName() + " 5");
                        }

                        return;
                    }

                    if(this.plugin.getChatFilter().messageContainsOnlineMinecraftServerIP(packet.getMessage())) {

                        if(!this.plugin.getAdvertisingCount().containsKey(player.getUniqueId())) {
                            this.plugin.getAdvertisingCount().put(player.getUniqueId(), 1);
                        } else {
                            this.plugin.getAdvertisingCount().put(player.getUniqueId(), this.plugin.getAdvertisingCount().get(player.getUniqueId()) + 1);
                        }

                        addToPlayerChatLogs(player, packet.getMessage());
                        player.sendMessage(this.plugin.getPrefix() + "§cDu darfst keine Werbung für Server machen. (§e" + this.plugin.getAdvertisingCount().get(player.getUniqueId()) + "§c/§e3§c)");

                        if(this.plugin.getAdvertisingCount().get(player.getUniqueId()).equals(3)) {
                            this.plugin.getAdvertisingCount().remove(player.getUniqueId());

                            addToPlayerChatLogs(player, packet.getMessage());
                            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "abuse " + player.getName() + " 4");
                        }

                        return;
                    }

                }

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
