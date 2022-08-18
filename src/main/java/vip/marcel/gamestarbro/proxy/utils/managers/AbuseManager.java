package vip.marcel.gamestarbro.proxy.utils.managers;

import vip.marcel.gamestarbro.proxy.Proxy;
import vip.marcel.gamestarbro.proxy.utils.entities.AbusedInfo;
import vip.marcel.gamestarbro.proxy.utils.enums.AbuseType;

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

            //TODO: Disconnect player, send message to staff chat

        } else if(abuseType.equals(AbuseType.MUTE)) {
            this.plugin.getMuteAbuse().createMute(abusedInfo);
            this.plugin.getAllAbuseMutes().createMute(abusedInfo);

            //TODO: Send message to staff chat, player
        }
        return false;
    }

    public boolean deleteAbuse(AbuseType abuseType, UUID uuid) {
        if(abuseType.equals(AbuseType.BAN)) {
            this.plugin.getBanAbuse().deleteBan(uuid);

            //TODO: Send message to staff chat

        } else if(abuseType.equals(AbuseType.MUTE)) {
            this.plugin.getMuteAbuse().deleteMute(uuid);

            //TODO: Send message to staff chat, player

        }
        return false;
    }

    public AbusedInfo getAbuse(AbuseType abuseType, UUID uuid) {
        if(abuseType.equals(AbuseType.BAN)) {
            this.plugin.getBanAbuse().getAbuse(uuid);
        } else if(abuseType.equals(AbuseType.MUTE)) {
            this.plugin.getMuteAbuse().getAbuse(uuid);
        }
        return null;
    }

}
