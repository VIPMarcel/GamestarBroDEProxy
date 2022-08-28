package vip.marcel.gamestarbro.proxy.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.function.Consumer;

public class DiscordStaffBOT {

    private final Proxy plugin;
    private JDABuilder jdaBuilder;
    private JDA jda;

    private final TextChannel abusesChannel;
    private final TextChannel reportsChannel;
    private final TextChannel informationChannel;

    public DiscordStaffBOT(Proxy plugin) {
        this.plugin = plugin;

        this.initBOT();
        this.abusesChannel = this.jda.getTextChannelById("1013450527725600858");
        this.reportsChannel = this.jda.getTextChannelById("1013450766054326384");
        this.informationChannel = this.jda.getTextChannelById("1001192598448373891");
    }

    public void initBOT() {
        try {
            this.jdaBuilder = JDABuilder.createDefault(StaffBOTToken.TOKEN);
            this.jdaBuilder.setActivity(Activity.watching("GamestarBro Server"));
            this.jdaBuilder.setStatus(OnlineStatus.ONLINE);
            this.jda = this.jdaBuilder.build().awaitReady();
        } catch(LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdownBOT() {
        new Thread(() -> {
            if(this.jdaBuilder != null && this.jda != null) {
                this.jdaBuilder.setStatus(OnlineStatus.OFFLINE);
                this.jda.shutdown();
            }
        }).start();
    }

    public void sendKickMessage(String playername, String abusedBy, String reason) {
        if(this.abusesChannel == null) {
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("⚠ | Kick");
        builder.setDescription("Spieler » " + playername + "\n"
                + "Von » " + abusedBy + "\n"
                + "Grund » " + reason);
        builder.setColor(Color.ORANGE);

        this.abusesChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public void sendAbuseMessage(AbuseType abuseType, String playername, String abusedBy, String abuseId, String duration, String reason) {
        if(this.abusesChannel == null) {
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();

        if(abuseType.equals(AbuseType.BAN)) {
            builder.setTitle("✗ | Bann");
            builder.setDescription("Spieler » " + playername + " | AbuseId » " + abuseId + "\n"
                    + "Von » " + abusedBy + "\n"
                    + "Dauer » " + duration + "\n"
                    + "Grund » " + reason);
            builder.setColor(Color.RED);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            builder.setTitle("✗ | Mute");
            builder.setDescription("Spieler » " + playername + " | AbuseId » " + abuseId + "\n"
                    + "Von » " + abusedBy + "\n"
                    + "Dauer » " + duration + "\n"
                    + "Grund » " + reason);
            builder.setColor(Color.RED);
        }

        this.abusesChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public void sendPardonMessage(AbuseType abuseType, String playername, String pardonedBy, String abuseId) {
        if(this.abusesChannel == null) {
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();

        if(abuseType.equals(AbuseType.BAN)) {
            builder.setTitle("✓ | Un-Bann");
            builder.setDescription("Spieler » " + playername + " | AbuseId » " + abuseId + "\n"
                    + "Von » " + pardonedBy);
            builder.setColor(Color.GREEN);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            builder.setTitle("✓ | Un-Mute");
            builder.setDescription("Spieler » " + playername + " | AbuseId » " + abuseId + "\n"
                    + "Von » " + pardonedBy);
            builder.setColor(Color.GREEN);
        }

        this.abusesChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public void sendNewReportMessage(String reported, String reason, String reporter, Consumer<String> consumer) {
        if(this.reportsChannel == null) {
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("⚠ | Report");
        builder.setDescription("Spieler » " + reported + "\n"
                + "Von » " + reporter + "\n"
                + "Grund » " + reason + "\n\n"
                + this.jda.getRoleById("1013532127603740774").getAsMention());
        builder.setColor(Color.LIGHT_GRAY);

        this.reportsChannel.sendMessageEmbeds(builder.build()).queue(message -> consumer.accept(message.getId()));
    }

    public void updateReportMessage(String messageId, Color color, String reported, String reason, String reporter, String staffMember) {
        if(this.reportsChannel == null) {
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();

        if(color.equals(Color.GREEN)) {
            builder.setTitle("⚠ | Report");
            builder.setDescription("Spieler » " + reported + "\n"
                    + "Von » " + reporter + "\n"
                    + "Grund » " + reason + "\n\n"
                    + "Angenommen von » " + staffMember);
            builder.setColor(color);
        } else if(color.equals(Color.RED)) {
            builder.setTitle("⚠ | Report");
            builder.setDescription("Spieler » " + reported + "\n"
                    + "Von » " + reporter + "\n"
                    + "Grund » " + reason + "\n\n"
                    + "Abgelehnt von » " + staffMember);
            builder.setColor(color);
        }

        this.reportsChannel.editMessageEmbedsById(messageId, builder.build()).queue();
    }

    public void alertInformation(String message, String sendBy) {
        if(this.informationChannel == null) {
            return;
        }
        final EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(":loudspeaker: | Information");
        builder.setDescription(message);
        builder.setAuthor(sendBy);
        builder.setColor(Color.YELLOW);

        this.informationChannel.sendMessageEmbeds(builder.build()).queue();
    }

}
