package vip.marcel.gamestarbro.proxy.utils.database.abuse;

import com.google.common.collect.Lists;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class AllAbuseBans {

    private final Proxy plugin;

    public AllAbuseBans(Proxy plugin) {
        this.plugin = plugin;
    }

    public boolean isExisting(String abuseId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE AbuseId = ?");
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
        LinkedList<String> output = Lists.newLinkedList();

        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE UUID = ? ORDER BY id");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                output.addLast(resultSet.getString("AbuseId"));
            }

            resultSet.close();
            statement.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return output;
    }

    public List<String> getAllAbuseIds() {
        List<String> output = new ArrayList<>();

        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans");
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

    public boolean createBan(AbusedInfo abuse) {
        if(!this.isExisting(abuse.getAbuseId())) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO AllAbuseBans(UUID, AbusedBy, AbuseReason, AbuseId, AbuseCreated, AbuseEnd) VALUES (?, ?, ?, ?, ?, ?)");
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

    public boolean deleteBan(String abuseId) {
        if(this.isExisting(abuseId)) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("DELETE FROM AllAbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AllAbuseBans WHERE AbuseId = ?");
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
