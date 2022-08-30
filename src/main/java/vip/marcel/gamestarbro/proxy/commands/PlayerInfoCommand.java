package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
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
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerInfoCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public PlayerInfoCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um seine Informationen zu sehen.");
            return;
        }

        final UUID uuid = UUIDFetcher.getUUID(arguments[0]);
        final String name = UUIDFetcher.getName(uuid);

        if(uuid == null) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
            return;
        }

        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {

            sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m---------------------§r§8┃ §cInfo §8§m┃----------------------");

            if(ProxyServer.getInstance().getPlayer(uuid) != null) {
                final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);

                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Name §8» §e" + target.getName());
                TextComponent server = new TextComponent(ProxyServer.getInstance().getPlayer(uuid).getServer().getInfo().getName());
                server.setColor(ChatColor.YELLOW);
                server.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §aZu ihm springen").create()));
                server.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + ProxyServer.getInstance().getPlayer(uuid).getServer().getInfo().getName()));
                sender.sendMessage(new TextComponent(this.plugin.getTeamPrefix() + "§7Status §8» §aOnline §8» "), server);
                sender.sendMessage(this.plugin.getTeamPrefix());

                if(sender.hasPermission("proxy.admin")) {
                    TextComponent ipAdress = new TextComponent("HIDDEN");
                    ipAdress.setColor(ChatColor.GREEN);
                    ipAdress.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a" + target.getAddress().toString().split(":")[0].replaceFirst("/", "")).create()));
                    ipAdress.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, target.getAddress().toString().split(":")[0].replaceFirst("/", "")));
                    sender.sendMessage(new TextComponent(this.plugin.getTeamPrefix() + "§7IP-Adresse §8» §e"), ipAdress);
                    sender.sendMessage(this.plugin.getTeamPrefix());
                }

            }
            else {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Name §8» §e" + name);
                sender.sendMessage(this.plugin.getTeamPrefix());
                if(this.plugin.getDatabasePlayers().doesPlayerExists(uuid)) {

                    if(sender.hasPermission("proxy.admin")) {
                        TextComponent ipAdress = new TextComponent("HIDDEN");
                        ipAdress.setColor(ChatColor.GREEN);
                        ipAdress.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a" + this.plugin.getDatabasePlayers().getIPAdress(uuid)).create()));
                        ipAdress.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.plugin.getDatabasePlayers().getIPAdress(uuid)));
                        sender.sendMessage(new TextComponent(this.plugin.getTeamPrefix() + "§7Letzte IP-Adresse §8» §e"), ipAdress);
                        sender.sendMessage(this.plugin.getTeamPrefix());
                    }

                    sender.sendMessage(this.plugin.getTeamPrefix() + "§7Status §8» §cOffline");
                } else {
                    sender.sendMessage(this.plugin.getTeamPrefix() + "§7Status §8» §cNicht registriert");
                }
                sender.sendMessage(this.plugin.getTeamPrefix());
            }

            if(this.plugin.getDatabaseVerify().doesPlayerExists(uuid)) {
                TextComponent ipAdress = new TextComponent(this.plugin.getDatabaseVerify().getUserId(uuid));
                ipAdress.setColor(ChatColor.YELLOW);
                ipAdress.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §aKlicke um zu kopieren.").create()));
                ipAdress.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, this.plugin.getDatabaseVerify().getUserId(uuid)));
                sender.sendMessage(new TextComponent(this.plugin.getTeamPrefix() + "§7Discord-ID §8» §e"), ipAdress);
                sender.sendMessage(this.plugin.getTeamPrefix());
            }

            if(this.plugin.getDatabasePlayers().doesPlayerExists(uuid)) {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Spielzeit §8» §e" + this.plugin.getAbuseTimeManager().getSimpleTimeString(this.plugin.getDatabasePlayers().getPlayTime(uuid)));
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Login-Streak §8» §e" + this.plugin.getDatabasePlayers().getLoginStreak(uuid));
                sender.sendMessage(this.plugin.getTeamPrefix());
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Straflevel §8» §c" + this.plugin.getDatabasePlayers().getAbuseLevel(uuid));
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Kicks §8» §c" + this.plugin.getDatabasePlayers().getKicksAmount(uuid));
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Coins §8» §e" + this.plugin.getDatabasePlayers().getCoins(uuid));
                sender.sendMessage(this.plugin.getTeamPrefix());

                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(this.plugin.getDatabasePlayers().getFirstJoinMillis(uuid));
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    String dateString = sdf.format(date);

                    sender.sendMessage(this.plugin.getTeamPrefix() + "§7Beigetreten §8» §e" + dateString);
                }

                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(this.plugin.getDatabasePlayers().getLastSeenMillis(uuid));
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                    String dateString = sdf.format(date);

                    sender.sendMessage(this.plugin.getTeamPrefix() + "§7Zuletzt gesehen §8» §e" + dateString);
                }
                sender.sendMessage(this.plugin.getTeamPrefix());

            } else {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Spielzeit §8» §e" + "0 Sekunden");
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Login-Streak §8» §e" + "0");
                sender.sendMessage(this.plugin.getTeamPrefix());
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Straflevel §8» §c" + "0");
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Kicks §8» §c" + "0");
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Coins §8» §e" + "0");
                sender.sendMessage(this.plugin.getTeamPrefix());
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Beigetreten §8» §e" + "-/-");
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Zuletzt gesehen §8» §e" + "-/-");
                sender.sendMessage(this.plugin.getTeamPrefix());
            }

            if(this.plugin.getWhitelistedUUIDs().contains(uuid)) {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Global-Whitelistet §8» §aJa");
            }
            else {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Global-Whitelistet §8» §cNein");
            }

            if(this.plugin.getBlacklistedUUIDs().contains(uuid)) {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Geblacklistet §8» §aJa");
            }
            else {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Geblacklistet §8» §cNein");
            }

            final String lastIpAdress = this.plugin.getDatabasePlayers().getIPAdress(uuid);

            if(lastIpAdress != null) {
                if(this.plugin.getBlacklistedUUIDs().contains(uuid)) {
                    sender.sendMessage(this.plugin.getTeamPrefix() + "§7IP-Geblacklistet §8» §aJa");
                }
                else {
                    sender.sendMessage(this.plugin.getTeamPrefix() + "§7IP-Geblacklistet §8» §cNein");
                }
            }
            sender.sendMessage(this.plugin.getTeamPrefix());

            if(this.plugin.getAbuseManager().isAbuse(AbuseType.BAN, uuid)) {
                final AbusedInfo abusedInfo = this.plugin.getAbuseManager().getAbuse(AbuseType.BAN, uuid);

                TextComponent abuseId = new TextComponent(abusedInfo.getAbuseId());
                abuseId.setColor(ChatColor.GREEN);
                abuseId.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §eKlicke um den Abuse anzuzeigen").create()));
                abuseId.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkAbuse " + abusedInfo.getAbuseId()));
                sender.sendMessage(new TextComponent(this.plugin.getTeamPrefix() + "§7Abuse §8(§cBann§8) » §e"), abuseId);
            } else {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Abuse §8(§cBann§8) » §e-/-");
            }

            if(this.plugin.getAbuseManager().isAbuse(AbuseType.MUTE, uuid)) {
                final AbusedInfo abusedInfo = this.plugin.getAbuseManager().getAbuse(AbuseType.MUTE, uuid);

                TextComponent abuseId = new TextComponent(abusedInfo.getAbuseId());
                abuseId.setColor(ChatColor.GREEN);
                abuseId.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §eKlicke um den Abuse anzuzeigen").create()));
                abuseId.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/checkAbuse " + abusedInfo.getAbuseId()));
                sender.sendMessage(new TextComponent(this.plugin.getTeamPrefix() + "§7Abuse §8(§cMute§8) » §e"), abuseId);
            } else {
                sender.sendMessage(this.plugin.getTeamPrefix() + "§7Abuse §8(§cMute§8) » §e-/-");
            }
            sender.sendMessage(this.plugin.getTeamPrefix());
            sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");

        });

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(sender.hasPermission("proxy.command.playerinfo")) {
            if(arguments.length == 1) {
                for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                    output.add(players.getName());
                }
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
