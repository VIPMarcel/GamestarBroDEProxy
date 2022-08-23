package vip.marcel.gamestarbro.proxy.utils.managers;

import com.google.common.collect.Lists;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.Abuse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ConfigManager {

    private final Proxy plugin;

    private File file;
    private Configuration configuration;

    public ConfigManager(Proxy plugin) {
        this.plugin = plugin;

        if(!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        try {
            this.file = new File(this.plugin.getDataFolder().getPath(), "/config.yml");

            if(!this.file.exists()) {
                this.file.createNewFile();

                this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

                this.configuration.set("Database.MySQL.Hostname", "localhost");
                this.configuration.set("Database.MySQL.Port", 3306);
                this.configuration.set("Database.MySQL.Database", "network");
                this.configuration.set("Database.MySQL.Username", "root");
                this.configuration.set("Database.MySQL.Password", "");
                this.configuration.set("Server.Lobby-Server", "Lobby");
                this.configuration.set("Server.Bau-Server", "Bauserver");
                this.configuration.set("Server.Dev-Server", "Development");
                this.configuration.set("Server.Slots", 75);
                this.configuration.set("Server.FakePlayers", 0);
                this.configuration.set("Server.Maintenance.State", true);
                this.configuration.set("Server.Maintenance.Reason", "");
                this.configuration.set("Server.Maintenance.Allowed-UUIDs", Arrays.asList("d0a9dabf-7189-44fd-ae2b-04ac56de405d"));
                this.configuration.set("Server.Modt.1", "&6GamestarBro.de &8➼ &7Dein Netzwerk &8• &a1.19.x");
                this.configuration.set("Server.Modt.2", "&8  » &aWir haben geöffnet &7+ &etolle Preise");
                this.configuration.set("Server.Modt.Maintenance", "&8  » &cWir befinden uns in Wartungsarbeiten!");
                this.configuration.set("Server.Blacklisted-UUIDs", Lists.newArrayList());
                this.configuration.set("Server.Blacklisted-IPs", Lists.newArrayList());
                this.configuration.set("Server.Blacklisted-Words", Lists.newArrayList());
                this.saveConfig();

            }
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        } catch(IOException e) {
            e.printStackTrace();
        }

        File abuseJson = new File(this.plugin.getDataFolder().getPath() + "/abuse-reasons.json");
        if(!abuseJson.exists()) {
            try {
                final URL resource = getClass().getClassLoader().getResource("abuse-reasons.json");
                FileUtils.copyURLToFile(resource, abuseJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.reloadConfigIntoVariables();
        this.loadAbuseReasons();
    }

    public void reloadConfigIntoVariables() {

        try {
            this.plugin.setLobbyServerName(this.configuration.getString("Server.Lobby-Server"));
            this.plugin.setBauServerName(this.configuration.getString("Server.Bau-Server"));
            this.plugin.setDevServerName(this.configuration.getString("Server.Dev-Server"));
            this.plugin.setMaintenance(this.configuration.getBoolean("Server.Maintenance.State"));
            this.plugin.setServerSlots(this.configuration.getInt("Server.Slots"));
            this.plugin.setFakePlayers(this.configuration.getInt("Server.FakePlayers"));

            this.plugin.getBlacklistedWords().clear();
            this.plugin.getBlacklistedUUIDs().clear();
            this.plugin.getBlacklistedIPs().clear();
            this.plugin.getWhitelistedUUIDs().clear();

            if(!this.configuration.getStringList("Server.Maintenance.Allowed-UUIDs").isEmpty()) {
                this.configuration.getStringList("Server.Maintenance.Allowed-UUIDs").forEach(uuid -> {
                    this.plugin.getWhitelistedUUIDs().add(UUID.fromString(uuid));
                });
            }

            if(!this.configuration.getStringList("Server.Blacklisted-UUIDs").isEmpty()) {
                this.configuration.getStringList("Server.Blacklisted-UUIDs").forEach(uuid -> {
                    this.plugin.getBlacklistedUUIDs().add(UUID.fromString(uuid));
                });
            }

            if(!this.configuration.getStringList("Server.Blacklisted-IPs").isEmpty()) {
                this.configuration.getStringList("Server.Blacklisted-IPs").forEach(ip -> {
                    this.plugin.getBlacklistedIPs().add(ip);
                });
            }

            if(!this.configuration.getStringList("Server.Blacklisted-Words").isEmpty()) {
                this.configuration.getStringList("Server.Blacklisted-Words").forEach(word -> {
                    this.plugin.getBlacklistedWords().add(word);
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void loadAbuseReasons() {
        this.plugin.getAbuseReasons().clear();
        this.plugin.getAbuseIds().clear();

        JSONParser parser = new JSONParser();
        File abuseJson = new File(this.plugin.getDataFolder().getPath() + "/abuse-reasons.json");

        JSONArray abuseReasons = null;
        try {
            abuseReasons = (JSONArray) parser.parse(new FileReader(abuseJson));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Object object : abuseReasons) {
            JSONObject abuse = (JSONObject) object;

            Abuse abuseEntity = new Abuse();
            abuseEntity.setAbuseReason((String) abuse.get("AbuseReason"));
            abuseEntity.setAbusePermissionNeed((String) abuse.get("AbusePermission"));
            abuseEntity.setAbuseId(Integer.parseInt(String.valueOf(abuse.get("AbuseId"))));

            JSONArray abuseTimes = (JSONArray) abuse.get("Times");

            ArrayList<String> timeList = new ArrayList<>();

            for (Object obj : abuseTimes) {
                timeList.add((String) obj);
            }

            abuseEntity.setAbuseDurations(timeList);

            this.plugin.getAbuseReasons().put(abuseEntity.getAbuseReason(), abuseEntity);
            this.plugin.getAbuseIds().put(abuseEntity.getAbuseId(), abuseEntity);
        }
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
