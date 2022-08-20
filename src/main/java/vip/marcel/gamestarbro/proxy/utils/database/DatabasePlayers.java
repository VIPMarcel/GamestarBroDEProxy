package vip.marcel.gamestarbro.proxy.utils.database;

import vip.marcel.gamestarbro.proxy.Proxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabasePlayers {

    private final Proxy plugin;

    public DatabasePlayers(Proxy plugin) {
        this.plugin = plugin;
    }

    public boolean doesPlayerExists(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
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

    public void createPlayer(UUID uuid) {
        if(!doesPlayerExists(uuid)) {
            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO Players(UUID, PlayerName, IPAdress, AbuseLevel, KicksAmount, PlayTime, Coins, FirstJoin, LastSeen) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, "");
                statement.setString(3, "");
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.setLong(6, 0);
                statement.setInt(7, 0);
                statement.setLong(8, System.currentTimeMillis());
                statement.setLong(9, System.currentTimeMillis());
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
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("DELETE FROM Players WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
                statement.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public UUID getUuid(String playerName) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE PlayerName = ?");
            statement.setString(1, playerName);

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

    public String getPlayerName(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getString("PlayerName");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIPAdress(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getString("IPAdress");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getAbuseLevel(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getInt("AbuseLevel");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getKicksAmount(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getInt("KicksAmount");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getPlayTime(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getLong("PlayTime");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getCoins(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getInt("Coins");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getFirstJoinMillis(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getLong("FirstJoin");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getLastSeenMillis(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM Players WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                return resultSet.getLong("LastSeen");
            }

            statement.close();
            resultSet.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setPlayerName(UUID uuid, String playerName) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET PlayerName = ? WHERE UUID = ?");
            statement.setString(1, playerName);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setIPAdress(UUID uuid, String ipAdress) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET IPAdress = ? WHERE UUID = ?");
            statement.setString(1, ipAdress);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setAbuseLevel(UUID uuid, int abuseLevel) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET AbuseLevel = ? WHERE UUID = ?");
            statement.setInt(1, abuseLevel);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setKicksAmount(UUID uuid, int kicksAmount) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET KicksAmount = ? WHERE UUID = ?");
            statement.setInt(1, kicksAmount);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPlayTime(UUID uuid, long playTime) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET PlayTime = ? WHERE UUID = ?");
            statement.setLong(1, playTime);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCoins(UUID uuid, int coins) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET Coins = ? WHERE UUID = ?");
            statement.setInt(1, coins);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLastSeenMillis(UUID uuid, long lastSeenMillis) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("UPDATE Players SET LastSeen = ? WHERE UUID = ?");
            statement.setLong(1, lastSeenMillis);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
