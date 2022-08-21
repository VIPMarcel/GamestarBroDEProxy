package vip.marcel.gamestarbro.proxy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.Protocolize;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.protocol.packet.ClientChat;
import vip.marcel.gamestarbro.proxy.commands.*;
import vip.marcel.gamestarbro.proxy.listener.*;
import vip.marcel.gamestarbro.proxy.utils.database.DatabasePlayers;
import vip.marcel.gamestarbro.proxy.utils.database.MySQL;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.AllAbuseBans;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.AllAbuseMutes;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.BanAbuse;
import vip.marcel.gamestarbro.proxy.utils.database.abuse.MuteAbuse;
import vip.marcel.gamestarbro.proxy.utils.database.chatlog.ChatLog;
import vip.marcel.gamestarbro.proxy.utils.entities.Abuse;
import vip.marcel.gamestarbro.proxy.utils.fetcher.UUIDFetcher;
import vip.marcel.gamestarbro.proxy.utils.managers.AbuseManager;
import vip.marcel.gamestarbro.proxy.utils.managers.AbuseTimeManager;
import vip.marcel.gamestarbro.proxy.utils.managers.ConfigManager;
import vip.marcel.gamestarbro.proxy.utils.managers.PermissionsManager;

import java.util.LinkedList;
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
    private List<ProxiedPlayer> staffNotifyToggle;
    private List<ProxiedPlayer> onlineStaff;

    private String lobbyServerName, devServerName, bauServerName;
    private int serverSlots, fakePlayers;

    private boolean maintenance;

    private LuckPerms luckPerms;

    private Map<String, Abuse> abuseReasons;
    private Map<Integer, Abuse> abuseIds;

    private Map<UUID, LinkedList<String>> sendChatMessages;
    private Map<ProxiedPlayer, ServerInfo> joinMe;

    private MySQL mySQL;
    private BanAbuse banAbuse;
    private MuteAbuse muteAbuse;
    private AllAbuseBans allAbuseBans;
    private AllAbuseMutes allAbuseMutes;
    private DatabasePlayers databasePlayers;

    private ChatLog chatLog;

    private ConfigManager configManager;
    private AbuseTimeManager abuseTimeManager;
    private AbuseManager abuseManager;
    private PermissionsManager permissionsManager;

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
        this.staffNotifyToggle = Lists.newArrayList();
        this.onlineStaff = Lists.newArrayList();
        this.abuseReasons = Maps.newHashMap();
        this.abuseIds = Maps.newHashMap();

        this.sendChatMessages = Maps.newHashMap();
        this.joinMe = Maps.newHashMap();

        this.configManager = new ConfigManager(this);
        this.abuseTimeManager = new AbuseTimeManager(this);
        this.abuseManager = new AbuseManager(this);
        this.permissionsManager = new PermissionsManager(this);

        this.mySQL = new MySQL(this);
        this.mySQL.connect();
        this.banAbuse = new BanAbuse(this);
        this.muteAbuse = new MuteAbuse(this);
        this.allAbuseBans = new AllAbuseBans(this);
        this.allAbuseMutes = new AllAbuseMutes(this);
        this.databasePlayers = new DatabasePlayers(this);

        this.chatLog = new ChatLog(this);

        this.luckPerms = LuckPermsProvider.get();

        final PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerCommand(this, new AbuseCommand(this, "abuse", "proxy.command.abuse"));
        pluginManager.registerCommand(this, new PardonCommand(this, "pardon", "proxy.command.pardon"));
        pluginManager.registerCommand(this, new PardonIdCommand(this, "pardonId", "proxy.command.pardonId"));
        pluginManager.registerCommand(this, new CheckAbuseCommand(this, "checkAbuse", "proxy.command.checkAbuse"));
        pluginManager.registerCommand(this, new CheckAbusesCommand(this, "checkAbuses", "proxy.command.checkAbuses"));
        pluginManager.registerCommand(this, new KickCommand(this, "kick", "proxy.command.kick"));
        pluginManager.registerCommand(this, new ChatLogCommand(this, "chatlog", "proxy.command.chatlog"));

        pluginManager.registerListener(this, new LoginListener(this));
        pluginManager.registerListener(this, new ChatListener(this));
        pluginManager.registerListener(this, new ProxyPingListener(this));
        pluginManager.registerListener(this, new ServerKickListener(this));
        pluginManager.registerListener(this, new ServerConnectListener(this));
        pluginManager.registerListener(this, new ServerConnectedListener(this));
        pluginManager.registerListener(this, new PlayerDisconnectListener(this));
        pluginManager.registerListener(this, new ServerDisconnectListener(this));

        Protocolize.listenerProvider().registerListener(new ChatPacketListener(this, ClientChat.class, Direction.UPSTREAM, 0));
    }

    public boolean hasPermission(UUID uuid, String permission) {
        return this.permissionsManager.hasPermission(uuid, permission);
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
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

    public DatabasePlayers getDatabasePlayers() {
        return this.databasePlayers;
    }

    public ChatLog getChatLog() {
        return this.chatLog;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public AbuseTimeManager getAbuseTimeManager() {
        return this.abuseTimeManager;
    }

    public AbuseManager getAbuseManager() {
        return this.abuseManager;
    }

    public PermissionsManager getPermissionsManager() {
        return this.permissionsManager;
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

    public Map<UUID, LinkedList<String>> getSendChatMessages() {
        return this.sendChatMessages;
    }

    public Map<ProxiedPlayer, ServerInfo> getJoinMe() {
        return this.joinMe;
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

    public List<ProxiedPlayer> getStaffNotifyToggle() {
        return this.staffNotifyToggle;
    }

    public List<ProxiedPlayer> getOnlineStaff() {
        return this.onlineStaff;
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
