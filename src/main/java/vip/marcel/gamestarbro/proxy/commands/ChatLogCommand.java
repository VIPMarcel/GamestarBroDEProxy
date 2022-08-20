package vip.marcel.gamestarbro.proxy.commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatLogCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public ChatLogCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAbuseId §7angeben, um die letzten Nachrichten zu sehen.");
            return;
        }
        final String abuseId = arguments[0];

        if(!this.plugin.getChatLog().isExists(abuseId)) {
            sender.sendMessage(this.plugin.getPrefix() + "Keine ChatLogs von der AbuseId §e" + abuseId + " §7gefunden.");
            return;
        }

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m----------------------§r§8┃ §cLogs §8§m┃--------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "Spieler §8» §e" + UUIDFetcher.getName(this.plugin.getChatLog().getUuidFromAbuseId(abuseId)) + " §8┃ §7AbuseId §8» §e" + abuseId);
        sender.sendMessage(this.plugin.getTeamPrefix());

        this.plugin.getChatLog().getLastLogs(abuseId, 20).forEach(message -> {
            sender.sendMessage(this.plugin.getTeamPrefix() + message);
        });

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m-----------------------------------------------");

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = Lists.newArrayList();

        if(arguments.length == 1) {
            output.addAll(this.plugin.getAllAbuseBans().getAllAbuseIds());
            output.addAll(this.plugin.getAllAbuseMutes().getAllAbuseIds());
            output.addAll(this.plugin.getAbuseManager().getAllActiveAbuseIds());
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
