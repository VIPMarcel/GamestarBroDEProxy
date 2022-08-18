package vip.marcel.gamestarbro.proxy.utils.database.abuse;

import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AllAbuseMutes {

    private final Proxy plugin;

    public AllAbuseMutes(Proxy plugin) {
        this.plugin = plugin;
    }

    public boolean isExisting(String abuseId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE AbuseId = ?");
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

    public List<String> getAllAbuseIds(UUID uuid) {
        List<String> output = new ArrayList<>();

        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE UUID = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                output.add(resultSet.getString("AbuseId"));
            }

            resultSet.close();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return output;
    }

    public boolean createMute(AbusedInfo abuse) {
        if(!this.isExisting(abuse.getAbuseId())) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO AllAbuseMutes(UUID, AbusedBy, AbuseReason, AbuseId, AbuseCreated, AbuseEnd) VALUES (?, ?, ?, ?, ?, ?)");
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

    public boolean deleteMute(String abuseId) {
        if(this.isExisting(abuseId)) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("DELETE FROM AllAbuseMutes WHERE AbuseId = ?");
                statement.setString(1, abuseId);
                statement.executeUpdate();
                statement.close();

                return true;
            } catch(SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public AbusedInfo getAbuse(String abuseId) {
        AbusedInfo abusedInfo = new AbusedInfo();

        if(isExisting(abuseId)) {

            abusedInfo.setAbuseId(abuseId);

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE AbuseId = ?");
                    statement.setString(1, abuseId);

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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseMutes WHERE AbuseId = ?");
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
