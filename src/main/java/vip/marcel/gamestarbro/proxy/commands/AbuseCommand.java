package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.Abuse;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AbuseCommand extends Command {

    private final Proxy plugin;

    public AbuseCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um ihn zu bestrafen.");
            return;
        }

        final UUID uuid = UUIDFetcher.getUUID(arguments[0]);
        final String name = UUIDFetcher.getName(uuid);

        if(uuid == null) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7existiert nicht.");
            return;
        }

        if(arguments.length == 1) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst eine §eAbuseId §7angeben, um §e" + name + " §7zu bestrafen.");

            this.plugin.getAbuseIds().keySet().forEach(abuseIds -> {
                final Abuse abuse = this.plugin.getAbuseIds().get(abuseIds);
                if(sender.hasPermission(abuse.getAbusePermissionNeed())) {
                    sender.sendMessage(this.plugin.getPrefix() + "§e" + abuseIds + " §8» §7" + abuse.getAbuseReason());
                }
            });

            return;
        }

        int abuseId;

        try {
            abuseId = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Wert §e" + arguments[1] + " §7ist keine gültige Zahl.");
            return;
        }

        if(!this.plugin.getAbuseIds().containsKey(abuseId)) {
            sender.sendMessage(this.plugin.getPrefix() + "Die AbuseId §e" + abuseId + " §7existiert nicht.");
            return;
        }

        final Abuse abuse = this.plugin.getAbuseIds().get(abuseId);

        //TODO: Do Player, files
        final int testAbuseLevel = 0;

        if(!sender.hasPermission(abuse.getAbusePermissionNeed())) {
            sender.sendMessage(this.plugin.getPrefix() + "§cDu darfst diese §eAbuseId §cnicht benutzen. Bitte ein §ehöheres Teammitglied §cum Hilfe.");
            return;
        }

        final String abuseDurationString = abuse.getAbuseDurations().get(testAbuseLevel);
        final int duration = Integer.parseInt(abuseDurationString.split(" ")[0]);
        final String format = abuseDurationString.split(" ")[1];
        final String abuseTypeString = abuseDurationString.split(" ")[2];

        AbuseType abuseType = null;
        String abuseTypePrefix = "";

        if(abuseTypeString.equalsIgnoreCase("ban")) {
            abuseType = AbuseType.BAN;
            abuseTypePrefix = "gebannt";
        }else if(abuseTypeString.equalsIgnoreCase("mute")) {
            abuseType = AbuseType.MUTE;
            abuseTypePrefix = "gemutet";
        }

        if(abuseType == null) {
            sender.sendMessage(this.plugin.getPrefix() + "§cDer AbuseType §e" + abuseTypeString + " §ckonnte nicht identifiziert werden.");
            return;
        }

        if(this.plugin.getAbuseManager().isAbuse(abuseType, uuid)) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + name + " §7ist bereits §e" + abuseTypePrefix + "§7.");
            return;
        }

        final AbusedInfo abusedInfo = new AbusedInfo();

        abusedInfo.setUuid(uuid);

        if(sender instanceof ProxiedPlayer player) {
            abusedInfo.setAbusedByName(player.getName());
        } else {
            abusedInfo.setAbusedByName("Automatisch | Server");
        }

        abusedInfo.setAbuseReason(abuse.getAbuseReason());
        abusedInfo.setAbuseId(this.plugin.getAbuseManager().generateAbuseId()); //TODO: Check if this number is not in database -> /checkAbuse <AbuseId> || /pardon <AbuseId>
        abusedInfo.setAbuseCreated(System.currentTimeMillis());

        long abuseExpiresMillis = System.currentTimeMillis();

        if(format.equalsIgnoreCase("s")) {
            abuseExpiresMillis += TimeUnit.SECONDS.toMillis(duration);
        } else if(format.equalsIgnoreCase("m")) {
            abuseExpiresMillis += TimeUnit.MINUTES.toMillis(duration);
        } else if(format.equalsIgnoreCase("h")) {
            abuseExpiresMillis += TimeUnit.HOURS.toMillis(duration);
        } else if(format.equalsIgnoreCase("d")) {
            abuseExpiresMillis += TimeUnit.DAYS.toMillis(duration);
        } else if(format.equalsIgnoreCase("permanent")) {
            abuseExpiresMillis = -1;
        }

        abusedInfo.setAbuseExpires(abuseExpiresMillis);

        this.plugin.getAbuseManager().createAbuse(abuseType, abusedInfo);



        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);

        String abuseTypeName = "";
        if(abuseType.equals(AbuseType.BAN)) {
            abuseTypeName = "Bann";
        } else if(abuseType.equals(AbuseType.MUTE)) {
            abuseTypeName = "Mute";
        }

        String finalAbuseTypeName = abuseTypeName;
        ProxyServer.getInstance().getPlayers().forEach(players -> {
            if(players.hasPermission("proxy.abuse.notify")) {
                if(!this.plugin.getStaffNotifyToggle().contains(players)) {
                    if(players != sender) {
                        sendAbuseStaffMessage(players, name, abusedInfo, finalAbuseTypeName);
                    }
                }
            }
        });

        sendAbuseStaffMessage(sender, name, abusedInfo, finalAbuseTypeName);

        if(sender != ProxyServer.getInstance().getConsole()) {
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m---------------------§r§8┃ §cAbuse §8§m┃--------------------");
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + name);
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Grund §8» §e" + abusedInfo.getAbusedByName());
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7AbuseType §8» " + "§e" + finalAbuseTypeName);
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Dauer §8» §e" + (abusedInfo.getAbuseExpires() == -1 ? "Permanent" : this.plugin.getAbuseTimeManager().getTempBanLength(abusedInfo.getAbuseExpires())));
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§7Von §8» §e" + abusedInfo.getAbusedByName());
            ProxyServer.getInstance().getConsole().sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
        }

        if(target != null) {
            if(abuseType.equals(AbuseType.BAN)) {

                target.disconnect("\n§8§m--------------------------------------------\n" +
                        "§cDu wurdest vom §6GamestarBro.de Netzwerk §causgeschlossen.\n\n" +

                        "§7Grund §8» §e" + abusedInfo.getAbuseReason() + "\n" +
                        "§7AbuseId §8» §e" + abusedInfo.getAbuseId() + "\n\n" +

                        "§7Dauer §8» §e" + (abusedInfo.getAbuseExpires() == -1 ? "Permanent" : this.plugin.getAbuseTimeManager().getTempBanLength(abusedInfo.getAbuseExpires())) + "\n" +
                        /*"§7Von §8» §e" + abusedInfo.getAbusedByName() + "\n*/"\n\n" +

                        "§7Nicht gerechtfertigt? §8» §aEröffne auf unserem Discord Server ein Ticket.\n" +
                        "§e§o" + "https://discord.gg/rrtCWGhrrV" + "\n\n" +

                        "§8§m--------------------------------------------\n");



            } else if(abuseType.equals(AbuseType.MUTE)) {

                target.sendMessage(" ");
                target.sendMessage(this.plugin.getPrefix() + "§cDu wurdest aus dem §eChat §causgeschlossen.");
                target.sendMessage(this.plugin.getPrefix() + "Grund §8» §e" + abusedInfo.getAbuseReason());
                target.sendMessage(this.plugin.getPrefix() + "AbuseId §8» §e" + abusedInfo.getAbuseId());
                //target.sendMessage(this.plugin.getPrefix() + "Von §8» §e" + abusedInfo.getAbusedByName());
                //target.sendMessage("§7Nicht gerechtfertigt? §8» §aEröffne auf unserem Discord Server ein Ticket.");
                target.sendMessage(" ");

            }
        }

    }

    private void sendAbuseStaffMessage(CommandSender sender, String name, AbusedInfo abusedInfo, String finalAbuseTypeName) {
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m---------------------§r§8┃ §cAbuse §8§m┃--------------------");
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Spieler §8» §e" + name);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Grund §8» §e" + abusedInfo.getAbusedByName());
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7AbuseType §8» " + "§e" + finalAbuseTypeName);
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Dauer §8» §e" + (abusedInfo.getAbuseExpires() == -1 ? "Permanent" : this.plugin.getAbuseTimeManager().getTempBanLength(abusedInfo.getAbuseExpires())));
        sender.sendMessage(this.plugin.getTeamPrefix() + "§7Von §8» §e" + abusedInfo.getAbusedByName());
        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------------------------------------");
    }

}
