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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WhereIsCommand extends Command implements TabExecutor {

    private final Proxy plugin;

    public WhereIsCommand(Proxy plugin, String name, String permission) {
        super(name, permission);
        this.setPermissionMessage(plugin.getPrefix() + plugin.getNoPermissions());
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + "Du musst einen §eSpielernamen §7angeben, um zu sehen auf welchem Server er sich grade befindet.");
            return;
        }
        final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(arguments[0]);

        if(target == null) {
            sender.sendMessage(this.plugin.getPrefix() + "Der Spieler §e" + arguments[0] + " §7ist nicht online.");
            return;
        }

        TextComponent server = new TextComponent(target.getServer().getInfo().getName());
        server.setColor(ChatColor.YELLOW);
        server.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §aZu ihm springen").create()));
        server.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + target.getServer().getInfo().getName()));

        sender.sendMessage(new TextComponent(this.plugin.getPrefix() + "§e" + target.getName() + " §7befindet sich momentan auf dem Server §8» "), server);

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] arguments) {
        List<String> output = new ArrayList<>();

        if(sender.hasPermission("proxy.staff")) {
            if(arguments.length == 1) {
                for(ProxiedPlayer players : ProxyServer.getInstance().getPlayers()) {
                    output.add(players.getName());
                }
            }
        }

        return output.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}
