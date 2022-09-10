package vip.marcel.gamestarbro.proxy.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang3.RandomStringUtils;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class VerifyCommand extends Command {

    private final Proxy plugin;

    public VerifyCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(sender instanceof ProxiedPlayer player) {

            if(arguments.length == 0) {
                player.sendMessage("§8§l┃ §aVerify §8► §7" + "Du musst deinen §eDiscordnamen §7angeben, um dich zu verifizieren.");
                player.sendMessage("§8§l┃ §aVerify §8► §7" + "Verifizierungen sind nur einmal Möglich und können nicht aufgehoben werden.");
                return;
            }

            if(arguments.length == 1) {
                String discordTag = arguments[0];

                final Guild guild = this.plugin.getDiscordStaffBOT().getJDA().getGuildById("750765250571141251");
                final Member member = guild.retrieveMembersByPrefix(discordTag, 10).get().get(0);

                if(member == null) {
                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "Es wurde kein Member mit dem Namen §e" + discordTag + " §7gefunden.");
                    return;
                }

                final String memberId = member.getId();

                if(this.plugin.getDatabaseVerify().doesPlayerExists(player.getUniqueId())) {
                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "§cDein Minecraft Account ist bereits mit Discord verifiziert.");
                    return;
                }

                if(this.plugin.getDatabaseVerify().doesPlayerExists(memberId)) {
                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "§cDer Discord Account ist bereits mit Minecraft verifiziert.");
                    return;
                }

                member.getUser().openPrivateChannel().queue(privateChannel -> {

                    final EmbedBuilder builder = new EmbedBuilder();
                    final String verifyCode = RandomStringUtils.randomAlphanumeric(10, 20);

                    builder.setTitle("✅ | Verifizieren");
                    builder.setDescription("Code » " + verifyCode + "\n\n"
                            + member.getAsMention() + ", schreibe diesen Code in den Minecraft-Chat, um dich zu verifizieren");
                    builder.setColor(Color.GREEN);

                    player.sendMessage("§8§l┃ §aVerify §8► §7" + "Checke deine §eDiscord Nachrichten §7nach dem §aCode§7. ");
                    privateChannel.sendMessageEmbeds(builder.build()).queue(success -> {
                        this.plugin.getVerifyCodeCheck().put(player, verifyCode);
                        this.plugin.getVerifyUserCheck().put(player, member.getUser());
                        success.delete().queueAfter(1, TimeUnit.MINUTES);
                    });
                });
            } else {
                player.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            }

        } else {
            sender.sendMessage(this.plugin.getPrefix() + "§cDer Befehl ist nur für echte Spieler geeignet.");
        }

    }

}
