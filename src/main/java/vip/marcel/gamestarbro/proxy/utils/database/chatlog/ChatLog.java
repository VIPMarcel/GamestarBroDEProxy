package vip.marcel.gamestarbro.proxy.utils.database.chatlog;

import net.md_5.bungee.api.ProxyServer;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatLog {

    private final Proxy plugin;

    public ChatLog(Proxy plugin) {
        this.plugin = plugin;
    }

    public void logChatMessages(UUID uuid, String abuseId, int lastMessagesCount) {
        lastMessagesCount++;
        final LinkedList<String> messages = this.plugin.getSendChatMessages().get(uuid);

        if(messages == null) {
            return;
        }

        if(messages.isEmpty()) {
            return;
        }

        if(messages.size() < lastMessagesCount) {
            lastMessagesCount = messages.size();
        }

        final List<String> lastMessages = messages.subList(messages.size() - lastMessagesCount, messages.size());
        Collections.reverse(lastMessages);

        int finalLastMessagesCount = lastMessagesCount;
        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, () -> {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm");
            String datePattern = sdf.format(date);

            lastMessages.forEach(message -> {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO ChatLog(UUID, AbuseId, Message) VALUES (?, ?, ?)");
                    statement.setString(1, uuid.toString());
                    statement.setString(2, abuseId);
                    statement.setString(3, "§e" + datePattern + " §8➠ §7" + "\"" + message + "\"");
                    statement.executeUpdate();
                    statement.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            });

        });
    }

    public boolean isExists(String abuseId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM ChatLog WHERE AbuseId = ?");
            statement.setString(1, abuseId);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
                return (resultSet.getString("AbuseId") != null);

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UUID getUuidFromAbuseId(String abuseId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM ChatLog WHERE AbuseId = ?");
            statement.setString(1, abuseId);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
                return UUID.fromString(resultSet.getString("UUID"));

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getLastLogs(String abuseId, int amount) {
        amount++;
        List<String> output = new LinkedList<>();

        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM ChatLog WHERE AbuseId = ? ORDER BY id DESC LIMIT ?");
            statement.setString(1, abuseId);
            statement.setInt(2, amount);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                output.add(resultSet.getString("Message"));
            }

            resultSet.close();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return output;
    }

}
