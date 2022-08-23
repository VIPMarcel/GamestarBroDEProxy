package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class ReloadBungeeSystemCommand extends Command {

    private final Proxy plugin;

    public ReloadBungeeSystemCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length != 0) {
            sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
            return;
        }

        this.plugin.getConfigManager().reloadConfigIntoVariables();

        final String motd1 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.1"));
        final String motd2 = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.2"));
        final String motdMaintenance = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfigManager().getConfiguration().getString("Server.Modt.Maintenance"));

        ProxyServer.getInstance().getPlayers().forEach(players -> {
            if(this.plugin.isMaintenance()) {
                players.setTabHeader(new TextComponent(motd1 + "\n" + motdMaintenance + "\n\n§7Partner §8➠ §bTube-Hosting.de\n"), new TextComponent("\n§7Server §8» §a" + players.getServer().getInfo().getName() + "\n\n§7Benutze §c/report §7wenn du einen §eRegelbrecher siehst.\n"));
            } else {
                players.setTabHeader(new TextComponent(motd1 + "\n" + motd2 + "\n\n§7Partner §8➠ §bTube-Hosting.de\n"), new TextComponent("\n§7Server §8» §a" + players.getServer().getInfo().getName() + "\n\n§7Benutze §c/report §7wenn du einen §eRegelbrecher siehst.\n"));
            }
        });

        sender.sendMessage(this.plugin.getPrefix() + "§eDu hast das Bungeecord- System neu geladen.");

    }

}
