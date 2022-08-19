package vip.marcel.gamestarbro.proxy.utils.managers;

import org.apache.commons.lang3.RandomStringUtils;
import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record AbuseManager(Proxy plugin) {

    public boolean isAbuse(AbuseType abuseType, UUID uuid) {
        if(abuseType.equals(AbuseType.BAN)) {
            return this.plugin.getBanAbuse().isBanned(uuid);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            return this.plugin.getMuteAbuse().isMuted(uuid);
        }
        return false;
    }

    public boolean createAbuse(AbuseType abuseType, AbusedInfo abusedInfo) {
        if(abuseType.equals(AbuseType.BAN)) {
            this.plugin.getBanAbuse().createBan(abusedInfo);
            this.plugin.getAllAbuseBans().createBan(abusedInfo);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            this.plugin.getMuteAbuse().createMute(abusedInfo);
            this.plugin.getAllAbuseMutes().createMute(abusedInfo);
        }
        return false;
    }

    public boolean deleteAbuse(AbuseType abuseType, UUID uuid) {
        if(abuseType.equals(AbuseType.BAN)) {
            this.plugin.getBanAbuse().deleteBan(uuid);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            this.plugin.getMuteAbuse().deleteMute(uuid);
        }
        return false;
    }

    public AbusedInfo getAbuse(AbuseType abuseType, UUID uuid) {
        if(abuseType.equals(AbuseType.BAN)) {
            return this.plugin.getBanAbuse().getAbuse(uuid);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            return this.plugin.getMuteAbuse().getAbuse(uuid);
        }
        return null;
    }

    public boolean isAbuseIdInUseBans(String abuseId) {
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

    public boolean isAbuseIdInUseAllBans(String abuseId) {
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

    public boolean isAbuseIdInUseMutes(String abuseId) {
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

    public boolean isAbuseIdInUseAllMutes(String abuseId) {
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

    public boolean isAbuseIdInUse(String abuseId) {
        if(isAbuseIdInUseBans(abuseId)) {
            return true;
        } else if(isAbuseIdInUseMutes(abuseId)) {
            return true;
        } else if(isAbuseIdInUseAllBans(abuseId)) {
            return true;
        } else if(isAbuseIdInUseAllMutes(abuseId)) {
            return true;
        } else {
            return false;
        }
    }

    public String generateAbuseId() {
        final String generatedAbuseId = "#" + RandomStringUtils.randomAlphanumeric(7);

        if(isAbuseIdInUse(generatedAbuseId)) {
            return generateAbuseId();
        } else {
            return generatedAbuseId;
        }
    }

}
