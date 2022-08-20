package vip.marcel.gamestarbro.proxy.commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CheckAbuseCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public CheckAbuseCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAbuseId §7angeben, um Informationen zu sehen.");
            return;
        }

        final String abuseId = arguments[0];

        if(!this.plugin.getAbuseManager().isAbuseIdInUse(abuseId)) {
            sender.sendMessage(this.plugin.getPrefix() + "Die AbuseId §e" + arguments[0] + " §7existiert nicht.");
            return;
        }

        AbusedInfo abusedInfo = null;
        String abuseTypeString = "";

        if(this.plugin.getBanAbuse().isBanned(abuseId)) {
            abusedInfo = this.plugin.getBanAbuse().getAbuse(abuseId);
            abuseTypeString = "Bann";
        } else if(this.plugin.getMuteAbuse().isMuted(abuseId)) {
            abusedInfo = this.plugin.getMuteAbuse().getAbuse(abuseId);
            abuseTypeString = "Mute";
        } else if(this.plugin.getAllAbuseBans().isExisting(abuseId)) {
            abusedInfo = this.plugin.getAllAbuseBans().getAbuse(abuseId);
            abuseTypeString = "Bann";
        } else if(this.plugin.getAllAbuseMutes().isExisting(abuseId)) {
            abusedInfo = this.plugin.getAllAbuseMutes().getAbuse(abuseId);
            abuseTypeString = "Mute";
        }

        if(abusedInfo == null) {
            sender.sendMessage(this.plugin.getPrefix() + "Die AbuseId §e" + abuseId + " §7ist fehlerhaft.");
            return;
        }

        final long abusedAtMillis = abusedInfo.getAbuseCreated();
        final long abusedExpiresAtMillis = abusedInfo.getAbuseExpires();
        final long abusedTimeMillis = abusedExpiresAtMillis - abusedAtMillis;

        String abusedAtString = "";
        String abusedExpiresAtString = "";
        String abuseLengthString = this.plugin.getAbuseTimeManager().getSimpleTimeString(TimeUnit.MILLISECONDS.toSeconds(abusedTimeMillis));

        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(abusedAtMillis);
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            abusedAtString = sdf.format(date);
        }

        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(abusedExpiresAtMillis);
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            abusedExpiresAtString = sdf.format(date);
        }

        if(abusedExpiresAtMillis == -1) {
            abusedExpiresAtString = "-/-";
            abuseLengthString = "Permanent";
        }

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m---------------------§r§8┃ §cInfo §8§m┃----------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "AbuseId §8» §e" + abusedInfo.getAbuseId());
        sender.sendMessage(this.plugin.getTeamPrefix() + "Spieler §8» §e" + UUIDFetcher.getName(abusedInfo.getUuid()));
        sender.sendMessage(this.plugin.getTeamPrefix() + "Grund §8» §e" + abusedInfo.getAbuseReason());
        sender.sendMessage(this.plugin.getTeamPrefix() + "AbuseType §8» §e" + abuseTypeString);
        sender.sendMessage(this.plugin.getTeamPrefix() + "Bestraft von §8» §e" + abusedInfo.getAbusedByName());
        sender.sendMessage(this.plugin.getTeamPrefix() + "Dauer §8» §e" + abuseLengthString);
        sender.sendMessage(this.plugin.getTeamPrefix() + "Strafe erstellt §8» §e" + abusedAtString);
        sender.sendMessage(this.plugin.getTeamPrefix() + "Strafe endet §8» §e" + abusedExpiresAtString);

        TextComponent textComponent = new TextComponent(this.plugin.getTeamPrefix());
        TextComponent list = new TextComponent("§a§oKlicke, um mögliche ChatLogs zu dem Abuse anzuzeigen");
        list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §eChatLogs anzeigen").create()));
        list.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatlog " + abusedInfo.getAbuseId()));
        textComponent.addExtra(list);
        sender.sendMessage(textComponent);

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");

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
