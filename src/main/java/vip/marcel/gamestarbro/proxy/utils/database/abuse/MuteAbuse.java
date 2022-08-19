package vip.marcel.gamestarbro.proxy.utils.database.abuse;

import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MuteAbuse {

    private final Proxy plugin;

    public MuteAbuse(Proxy plugin) {
        this.plugin = plugin;
    }

    public boolean isMuted(UUID uuid) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE UUID = ?");
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

    public boolean isMuted(String abuseId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE AbuseId = ?");
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

    public boolean createMute(AbusedInfo abuse) {
        if(!this.isMuted(abuse.getUuid())) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO AbuseMutes(UUID, AbusedBy, AbuseReason, AbuseId, AbuseCreated, AbuseEnd) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setString(1, abuse.getUuid().toString());
                statement.setString(2, abuse.getAbusedByName());
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

    public boolean deleteMute(UUID uuid) {
        if(this.isMuted(uuid)) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("DELETE FROM AbuseMutes WHERE UUID = ?");
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

        if(isMuted(uuid)) {

            abusedInfo.setUuid(uuid);

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE UUID = ?");
                    statement.setString(1, uuid.toString());

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbusedByName(resultSet.getString("AbusedBy"));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE UUID = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE UUID = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE UUID = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE UUID = ?");
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

    public AbusedInfo getAbuse(String abuseId) {
        AbusedInfo abusedInfo = new AbusedInfo();

        if(isMuted(abuseId)) {

            abusedInfo.setAbuseId(abuseId);

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE AbuseId = ?");
                    statement.setString(1, abuseId);

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setUuid(UUID.fromString(resultSet.getString("UUID")));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE AbuseId = ?");
                    statement.setString(1, abuseId);

                    ResultSet resultSet = statement.executeQuery();
                    while(resultSet.next())
                        abusedInfo.setAbusedByName(resultSet.getString("AbusedBy"));

                    statement.close();
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE AbuseId = ?");
                    statement.setString(1, abuseId);

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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE AbuseId = ?");
                    statement.setString(1, abuseId);

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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseMutes WHERE AbuseId = ?");
                    statement.setString(1, abuseId);

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
