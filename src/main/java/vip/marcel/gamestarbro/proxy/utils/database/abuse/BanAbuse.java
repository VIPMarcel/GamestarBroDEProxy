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

    public boolean isBanned(String abuseId) {
        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE AbuseId = ?");
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

    public boolean createBan(AbusedInfo abuse) {
        if(!this.isBanned(abuse.getUuid())) {

            try {
                PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("INSERT INTO AbuseBans(UUID, AbusedBy, AbuseReason, AbuseId, AbuseCreated, AbuseEnd) VALUES (?, ?, ?, ?, ?, ?)");
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

    public List<String> getAllAbuseIds() {
        LinkedList<String> output = Lists.newLinkedList();

        try {
            PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans ORDER BY id");
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
                        abusedInfo.setAbusedByName(resultSet.getString("AbusedBy"));

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

    public AbusedInfo getAbuse(String abuseId) {
        AbusedInfo abusedInfo = new AbusedInfo();

        if(isBanned(abuseId)) {

            abusedInfo.setAbuseId(abuseId);

            {
                try {
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE AbuseId = ?");
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
                    PreparedStatement statement = this.plugin.getMySQL().getConnection().prepareStatement("SELECT * FROM AbuseBans WHERE AbuseId = ?");
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
