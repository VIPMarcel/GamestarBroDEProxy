package vip.marcel.gamestarbro.proxy.utils.database.discord;

import vip.marcel.gamestarbro.proxy.Proxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseVerify {

    private final Proxy plugin;

    public DatabaseVerify(Proxy plugin) {
        this.plugin = plugin;
    }

    public boolean doesPlayerExists(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Verify WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
                return (resultSet.getString("UUID") != null);

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean doesPlayerExists(String userId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Verify WHERE UserId = ?");
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
                return (resultSet.getString("UserId") != null);

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createPlayer(UUID uuid, String userId) {
        if(!doesPlayerExists(uuid)) {
            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO Verify(UUID, UserId, VerifiedAt) VALUES (?, ?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, userId);
                statement.setLong(3, System.currentTimeMillis());
                statement.executeUpdate();
                statement.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deletePlayer(UUID uuid) {
        if(doesPlayerExists(uuid)) {
            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("DELETE FROM Verify WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
                statement.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public UUID getUuid(String userId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Verify WHERE UserId = ?");
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return UUID.fromString(resultSet.getString("UUID"));
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserId(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Verify WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getString("UserId");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getVerifiedAt(String userId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Verify WHERE UserId = ?");
            statement.setString(1, userId);

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getLong("VerifiedAt");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getVerifiedAt(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Verify WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getLong("VerifiedAt");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
