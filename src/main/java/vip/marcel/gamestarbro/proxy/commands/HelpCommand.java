package vip.marcel.gamestarbro.proxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import vip.marcel.gamestarbro.proxy.Proxy;

public class HelpCommand extends Command {

    private final Proxy plugin;

    public HelpCommand(Proxy plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

        if(arguments.length == 0) {

            sender.sendMessage("§8§m------------------------§r§8┃ §eHilfe §8§m┃-----------------------");

            sender.sendMessage("§8» §8/§ecoins §8┃ §7Zeigt dir deine Anzahl an Coins");
            sender.sendMessage("§8» §8/§ediscord §8┃ §7Betrete unseren Discord- Server");
            sender.sendMessage("§8» §8/§elobby §8┃ §7Kehre zurück zur Lobby");
            sender.sendMessage("§8» §8/§eonline §8┃ §7Zeigt dir die Onlinespieleranzahl");
            sender.sendMessage("§8» §8/§eping §8┃ §7Zeigt dir deinen Ping");
            sender.sendMessage("§8» §8/§emsg §8┃ §7Schreibe private Nachrichten");
            sender.sendMessage("§8» §8/§emsgtoggle §8┃ §7Deaktiviere private Nachrichten");
            sender.sendMessage("§8» §8/§ereport §8┃ §7Melde Regelbrecher");

            sender.sendMessage("§8§m-----------------------------------------------------");

        }
        else {
            sender.sendMessage(this.plugin.getPrefix() + this.plugin.getUnknownCommand());
        }

    }

}
