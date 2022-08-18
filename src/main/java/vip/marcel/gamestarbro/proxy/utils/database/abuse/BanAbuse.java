package vip.marcel.gamestarbro.proxy.utils.database.abuse;

import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BanAbuse {

    private final Proxy plugin;

    public BanAbuse(Proxy plugin) {
        this.plugin = plugin;
    }

    public boolean isBanned(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE UUID = ?");
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

    public boolean createBan(AbusedInfo abuse) {
        if(!this.isBanned(abuse.getUuid())) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO AbuseBans(UUID, AbusedBy, AbuseReason, AbuseId, AbuseCreated, AbuseEnd) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, abuse.getUuid().toString());
                statement.setString(2, abuse.getAbusedBy().toString());
                statement.setString(3, abuse.getAbuseReason());
                statement.setString(4, abuse.getAbuseId());
                statement.setLong(5, abuse.getAbuseCreated());
                statement.setLong(6, abuse.getAbuseExpires());
                statement.executeUpdate();
                statement.close();

                return true;
            } catch(SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public boolean deleteBan(UUID uuid) {
        if(this.isBanned(uuid)) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("DELETE FROM AbuseBans WHERE UUID = ?");
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
                statement.close();

                return true;
            } catch(SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public AbusedInfo getAbuse(UUID uuid) {
        AbusedInfo abusedInfo = new AbusedInfo();

        if(isBanned(uuid)) {

            abusedInfo.setUuid(uuid);

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE UUID = ?");
                    statement.setString(1, uuid.toString());

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbusedBy(UUID.fromString(resultSet.getString("AbusedBy")));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE UUID = ?");
                    statement.setString(1, uuid.toString());

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbuseReason(resultSet.getString("AbuseReason"));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE UUID = ?");
                    statement.setString(1, uuid.toString());

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbuseId(resultSet.getString("AbuseId"));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE UUID = ?");
                    statement.setString(1, uuid.toString());

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbuseCreated(resultSet.getLong("AbuseCreated"));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE UUID = ?");
                    statement.setString(1, uuid.toString());

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbuseExpires(resultSet.getLong("AbuseEnd"));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            return abusedInfo;
        }

        return null;
    }

}
