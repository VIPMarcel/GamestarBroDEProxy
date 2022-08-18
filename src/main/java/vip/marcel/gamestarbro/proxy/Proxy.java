package vip.marcel.gamestarbro.proxy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import vip.marcel.gamestarbro.proxy.commands.AbuseCommand;
import vip.marcel.gamestarbro.proxy.utils.database.MySQL;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.AllAbuseBans;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.AllAbuseMutes;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.BanAbuse;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.MuteAbuse;
import vip.marcel.gamestarbro.proxy.utils.entities.Abuse;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;
import vip.marcel.gamestarbro.proxy.utils.managers.AbuseManager;
import vip.marcel.gamestarbro.proxy.utils.managers.ConfigManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Proxy extends Plugin {

    private final String prefix = "§8§l┃ §6GamestarBro §8► §7";
    private final String teamPrefix = "§8§l┃§c • §8┃ §7"; //✚
    private final String noPermissions = "§cDu hast keinen Zugriff auf diesen Befehl.";
    private final String unknownCommand = "§cDieser Befehl existiert nicht.";

    private List<String> blacklistedWords;
    private List<UUID> blacklistedUUIDs, whitelistedUUIDs;

    private String lobbyServerName, devServerName, bauServerName;
    private int serverSlots, fakePlayers;

    private boolean maintenance;

    private Map<String, Abuse> abuseReasons;
    private Map<Integer, Abuse> abuseIds;

    private MySQL mySQL;
    private BanAbuse banAbuse;
    private MuteAbuse muteAbuse;
    private AllAbuseBans allAbuseBans;
    private AllAbuseMutes allAbuseMutes;

    private ConfigManager configManager;
    private AbuseManager abuseManager;

    @Override
    public void onEnable() {
        this.init();

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            UUIDFetcher.clearCache();
        }, 10, 10, TimeUnit.MINUTES);

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            this.mySQL.connect();
        }, 3, 3, TimeUnit.HOURS);

    }

    @Override
    public void onDisable() {
        this.mySQL.disconnect();
    }

    private void init() {
        this.blacklistedWords = Lists.newArrayList();
        this.blacklistedUUIDs = Lists.newArrayList();
        this.whitelistedUUIDs = Lists.newArrayList();
        this.abuseReasons = Maps.newHashMap();
        this.abuseIds = Maps.newHashMap();

        this.mySQL = new MySQL(this);
        this.mySQL.connect();
        this.banAbuse = new BanAbuse(this);
        this.muteAbuse = new MuteAbuse(this);
        this.allAbuseBans = new AllAbuseBans(this);
        this.allAbuseMutes = new AllAbuseMutes(this);

        this.configManager = new ConfigManager(this);
        this.abuseManager = new AbuseManager(this);

        final PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerCommand(this, new AbuseCommand(this, "abuse"));
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

    public BanAbuse getBanAbuse() {
        return this.banAbuse;
    }

    public MuteAbuse getMuteAbuse() {
        return this.muteAbuse;
    }

    public AllAbuseBans getAllAbuseBans() {
        return this.allAbuseBans;
    }

    public AllAbuseMutes getAllAbuseMutes() {
        return this.allAbuseMutes;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public AbuseManager getAbuseManager() {
        return this.abuseManager;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getTeamPrefix() {
        return this.teamPrefix;
    }

    public String getNoPermissions() {
        return this.noPermissions;
    }

    public String getUnknownCommand() {
        return this.unknownCommand;
    }

    public Map<String, Abuse> getAbuseReasons() {
        return this.abuseReasons;
    }

    public Map<Integer, Abuse> getAbuseIds() {
        return this.abuseIds;
    }

    public List<String> getBlacklistedWords() {
        return this.blacklistedWords;
    }

    public void setBlacklistedWords(List<String> blacklistedWords) {
        this.blacklistedWords = blacklistedWords;
    }

    public List<UUID> getBlacklistedUUIDs() {
        return this.blacklistedUUIDs;
    }

    public void setBlacklistedUUIDs(List<UUID> blacklistedUUIDs) {
        this.blacklistedUUIDs = blacklistedUUIDs;
    }

    public List<UUID> getWhitelistedUUIDs() {
        return this.whitelistedUUIDs;
    }

    public void setWhitelistedUUIDs(List<UUID> whitelistedUUIDs) {
        this.whitelistedUUIDs = whitelistedUUIDs;
    }

    public String getLobbyServerName() {
        return this.lobbyServerName;
    }

    public void setLobbyServerName(String lobbyServerName) {
        this.lobbyServerName = lobbyServerName;
    }

    public String getDevServerName() {
        return this.devServerName;
    }

    public void setDevServerName(String devServerName) {
        this.devServerName = devServerName;
    }

    public String getBauServerName() {
        return this.bauServerName;
    }

    public void setBauServerName(String bauServerName) {
        this.bauServerName = bauServerName;
    }

    public int getServerSlots() {
        return this.serverSlots;
    }

    public void setServerSlots(int serverSlots) {
        this.serverSlots = serverSlots;
    }

    public int getFakePlayers() {
        return this.fakePlayers;
    }

    public void setFakePlayers(int fakePlayers) {
        this.fakePlayers = fakePlayers;
    }

    public boolean isMaintenance() {
        return this.maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

}
