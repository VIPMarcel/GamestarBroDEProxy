package vip.marcel.gamestarbro.proxy.utils;

import org.apache.commons.validator.routines.InetAddressValidator;
import vip.marcel.gamestarbro.proxy.Proxy;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

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

    public boolean getDNSMinecraftServer(String address) {
        Hashtable env = new Hashtable();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        DirContext ctx = null;
        try {
            ctx = new InitialDirContext(env);
        } catch(NamingException ex) {}
        Attributes attrs = null;
        try {
            attrs = ctx.getAttributes("_minecraft._tcp." + address, new String[] { "SRV" });
        } catch (NamingException ex) {}

        try {
            if(!attrs.getAll().hasMore()) {
                return false;
            }
        } catch(NamingException | NullPointerException ex) {
            return false;
        }

        ArrayList<String> list = new ArrayList<>();

        NamingEnumeration<? extends Attribute> e = attrs.getAll();

        try {
            while(e.hasMore()) {
                list.add(e.next().toString());
            }
        } catch(NamingException ex) {}

        for(String s : list) {
            String hostname = s.split(" ")[4];
            hostname = hostname.substring(0, hostname.length() - 1);
            int port = Integer.valueOf(s.split(" ")[3]);
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(hostname, port), 250);
                try {
                    socket.close();
                } catch (IOException ex2) {}
                return true;
            } catch(IOException ex) {
                try {
                    socket.close();
                } catch(IOException ex2) {}
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
        message = message.replaceAll("\\]", "");
        message = message.replaceAll("\\{", "");
        message = message.replaceAll("\\}", "");
        message = message.replaceAll("\\<", "");
        message = message.replaceAll("\\>", "");

        String[] args = message.split(" ");

        for(String s : args) {
            if(s.contains(".")) {
                if(s.toLowerCase().contains("GamestarBro.de".toLowerCase())) {
                    return false;
                }
            }
        }

        for(int i = 0; i < args.length; i++) {
            if(i + 1 < args.length) {
                if(args[i].endsWith(".")) {
                    if((args[i] + args[i + 1]).toLowerCase().contains("GamestarBro.de".toLowerCase())) {
                        return false;
                    }
                }
            }
        }

        for(int i = 0; i < args.length; i++) {
            if(i + 1 < args.length) {
                if(args[i + 1].startsWith(".")) {
                    if((args[i] + args[i + 1]).toLowerCase().contains("GamestarBro.de".toLowerCase())) {
                        return false;
                    }
                }
            }
        }

        for(String s : args) {
            if(s.contains(".")) {
                return getDNSMinecraftServer(s);
            }
        }

        for(int i = 0; i < args.length; i++) {
            if(i + 1 < args.length) {
                if(args[i].endsWith(".")) {
                    return getDNSMinecraftServer(args[i] + args[i + 1]);
                }
            }
            i++;
        }

        for(int i = 0; i < args.length; i++) {
            if(i + 1 < args.length) {
                if(args[i + 1].startsWith(".")) {
                    return getDNSMinecraftServer(args[i] + args[i + 1]);
                }
            }
            i++;
        }

        final InetAddressValidator addressValidator = InetAddressValidator.getInstance();

        for(String s : args) {
            if(!s.equalsIgnoreCase("GamestarBro.de")) {
                try {
                    String ipAdress = InetAddress.getByName(s).getHostAddress();
                    if(ipAdress != null) {
                        if(addressValidator.isValid(ipAdress)) {
                            return true;
                        }
                    }
                } catch (Exception e) {}
            }
        }

        return false;
    }

}
