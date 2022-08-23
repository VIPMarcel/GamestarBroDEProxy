package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class StaffOnlineCommand extends Command {

    private final Proxy plugin;

    public StaffOnlineCommand(Proxy plugin, String name, String permission) {
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

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m------------------§r§8┃ §cOnlineTeam §8§m┃--------------------");

        for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
            if(this.plugin.getOnlineStaff().contains(players)) {
                sendServerMessages(sender, players);
            }
        }

        sender.sendMessage(this.plugin.getTeamPrefix() + "§8§m-------------------------------------------------");


    }

    private void sendServerMessages(CommandSender sender, ProxiedPlayer target) {
        TextComponent server = new TextComponent(target.getServer().getInfo().getName());
        server.setColor(ChatColor.YELLOW);
        server.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §aZu ihm springen").create()));
        server.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + target.getServer().getInfo().getName()));
        sender.sendMessage(new BaseComponent[] { new TextComponent(this.plugin.getTeamPrefix() + "§8» §c" + target.getName() + " §8» §e"), server });
    }

}
