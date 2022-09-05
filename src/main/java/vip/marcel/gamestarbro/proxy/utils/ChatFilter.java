package vip.marcel.gamestarbro.proxy.utils;

import vip.marcel.gamestarbro.proxy.Proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public record ChatFilter(Proxy plugin) {

    public boolean messageContainsBlackListedWords(String message) {
        for(String args : message.split(" ")) {
            for(String blacklist : this.plugin.getBlacklistedWords()) {
                if(args.equalsIgnoreCase(blacklist)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean messageContainsOnlineMinecraftServerIP(String message) {
        message = message.replaceAll(",", ".");
        message = message.replaceAll(";", ".");
        message = message.replaceAll("-", ".");
        message = message.replaceAll("_", ".");
        message = message.replaceAll("punkt", ".");
        message = message.replaceAll("\\(", "");
        message = message.replaceAll("\\)", "");
        message = message.replaceAll("\\[", "");
        message = message.replaceAll("]", "");
        message = message.replaceAll("\\{", "");
        message = message.replaceAll("}", "");
        message = message.replaceAll("<", "");
        message = message.replaceAll(">", "");

        String[] arguments = message.split(" ");

        for(String argument : arguments) {

            if(argument.equalsIgnoreCase("GamestarBro.de") | argument.equalsIgnoreCase("45.13.227.164")) {
                return false;
            }

            if(argument.contains(".")) {
                if(this.isAddressReachable(argument, 25565)) {
                    return true;
                }

                if(this.isAddressReachable(argument, 80)) {
                    return true;
                }
            }

        }

        return false;
    }

    private boolean isAddressReachable(String address, int port) {
        final Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(address, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

}
