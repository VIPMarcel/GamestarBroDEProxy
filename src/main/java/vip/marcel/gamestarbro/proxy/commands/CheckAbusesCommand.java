package vip.marcel.gamestarbro.proxy.commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CheckAbusesCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public CheckAbusesCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {

            int banAmount = this.plugin.getAllAbuseBans().getAllAbuseIds().size();
            int banActiveAmount = this.plugin.getBanAbuse().getAllAbuseIds().size();

            int muteAmount = this.plugin.getAllAbuseMutes().getAllAbuseIds().size();
            int muteActiveAmount = this.plugin.getMuteAbuse().getAllAbuseIds().size();

            sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um seine Strafen zu sehen.");

            if(banAmount == 1) {
                sender.sendMessage(this.plugin.getPrefix() + "Derzeit ist §e" + banAmount + " §7Bann registriert und §e" + banActiveAmount + " §7aktiv.");
            } else {
                sender.sendMessage(this.plugin.getPrefix() + "Derzeit sind §e" + banAmount + " §7Banns registriert und §e" + banActiveAmount + " §7aktiv.");
            }

            if(muteAmount == 1) {
                sender.sendMessage(this.plugin.getPrefix() + "Derzeit ist §e" + muteAmount + " §7Mute registriert und §e" + muteActiveAmount + " §7aktiv.");
            } else {
                sender.sendMessage(this.plugin.getPrefix() + "Derzeit sind §e" + muteAmount + " §7Mutes registriert und §e" + muteActiveAmount + " §7aktiv.");
            }
            return;
        }

        final UUID uuid = UUIDFetcher.getUUID(arguments[0]);
        final String name = UUIDFetcher.getName(uuid);

        if(uuid == null) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
            return;
        }

        final LinkedList<AbusedInfo> abuses = Lists.newLinkedList();

        if(this.plugin.getBanAbuse().getAbuse(uuid) != null) {
            abuses.addLast(this.plugin.getBanAbuse().getAbuse(uuid));
        }

        if(this.plugin.getMuteAbuse().getAbuse(uuid) != null) {
            abuses.addLast(this.plugin.getMuteAbuse().getAbuse(uuid));
        }

        if(!this.plugin.getAllAbuseBans().getAllAbuseIds(uuid).isEmpty()) {
            this.plugin.getAllAbuseBans().getAllAbuseIds(uuid).forEach(abuseId -> {
                abuses.addLast(this.plugin.getAllAbuseBans().getAbuse(abuseId));
            });
        }

        if(!this.plugin.getAllAbuseMutes().getAllAbuseIds(uuid).isEmpty()) {
            this.plugin.getAllAbuseMutes().getAllAbuseIds(uuid).forEach(abuseId -> {
                abuses.addLast(this.plugin.getAllAbuseMutes().getAbuse(abuseId));
            });
        }

        if(abuses.isEmpty()) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7hat keine vermerkten Strafen.");
            return;
        }

        Collections.reverse(abuses);

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m---------------------§r§8┃ §cInfo §8§m┃----------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "Spieler §8» §e" + name);
        sender.sendMessage(this.plugin.getTeamPrefix());

        abuses.forEach(abuse -> {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(abuse.getAbuseCreated());
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String abusedAtString = sdf.format(date);

            TextComponent textComponent = new TextComponent(this.plugin.getTeamPrefix());
            TextComponent textComponent2 = new TextComponent(" §8┃ §e" + abusedAtString + " §8┃ §7Grund §8» §e" + abuse.getAbuseReason());
            TextComponent list = new TextComponent("§a" + abuse.getAbuseId());
            list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §eAbuse anzeigen").create()));
            list.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkabuse " + abuse.getAbuseId()));
            textComponent.addExtra(list);
            textComponent.addExtra(textComponent2);
            sender.sendMessage(textComponent);

        });

        sender.sendMessage(this.plugin.getTeamPrefix());
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = Lists.newArrayList();

        if(arguments.length == 1) {
            for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                output.add(players.getName());
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
