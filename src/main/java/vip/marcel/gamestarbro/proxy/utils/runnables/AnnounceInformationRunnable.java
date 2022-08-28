package vip.marcel.gamestarbro.proxy.utils.runnables;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AnnounceInformationRunnable {

    private final Proxy plugin;

    private List<String> announceMessages;

    public AnnounceInformationRunnable(Proxy plugin) {
        this.plugin = plugin;

        this.announceMessages = Lists.newArrayList();

        this.initMessages();
    }

    private void initMessages() {
        this.announceMessages.add("Mit §e/report §7kannst du §cRegelbrecher §7melden.");
        this.announceMessages.add("Mit §e/discord §7kannst du unseren §eDiscord- Server §7beitreten.");
        this.announceMessages.add("Mit §e/help §7kannst du die §ewichtigsten Befehle §7sehen.");
        this.announceMessages.add("Du hast einen §cBug §7gefunden? Melde ihn im Discord.");
        this.announceMessages.add("Du hast einen §eVerbesserungsvorschlag§7? Teile ihn uns mit.");
        this.announceMessages.add("Du möchtest uns unterstützen? Eröffne ein §eTicket§7, im Discord.");
        this.announceMessages.add("Unser Partner §8» §bTube-Hosting.de");
    }

    public void run() {
        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {
            ProxyServer.getInstance().getScheduler().schedule(this.plugin, () -> {

                final String message = this.announceMessages.get(ThreadLocalRandom.current().nextInt(this.announceMessages.size()));

                for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                    players.sendMessage(" ");
                    players.sendMessage("§8§l┃ §b§l✧ §8► §7" + message);
                    players.sendMessage(" ");
                }

                ProxyServer.getInstance().getConsole().sendMessage(" ");
                ProxyServer.getInstance().getConsole().sendMessage("§8§l┃ §b§l✧ §8► §7" + message);
                ProxyServer.getInstance().getConsole().sendMessage(" ");

            }, 15, 15, TimeUnit.MINUTES);
        });
    }

}
