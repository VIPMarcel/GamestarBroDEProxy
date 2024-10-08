package vip.marcel.gamestarbro.proxy.utils.entities;

import java.util.UUID;

public class AbusedInfo {

    private UUID uuid;
    private String abusedByName;
    private String abuseReason;
    private String abuseId;
    private long abuseCreated;
    private long abuseExpires;

    public AbusedInfo() {
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAbusedByName() {
        return this.abusedByName;
    }

    public void setAbusedByName(String abusedByName) {
        this.abusedByName = abusedByName;
    }

    public String getAbuseReason() {
        return this.abuseReason;
    }

    public void setAbuseReason(String abuseReason) {
        this.abuseReason = abuseReason;
    }

    public String getAbuseId() {
        return this.abuseId;
    }

    public void setAbuseId(String abuseId) {
        this.abuseId = abuseId;
    }

    public long getAbuseCreated() {
        return this.abuseCreated;
    }

    public void setAbuseCreated(long abuseCreated) {
        this.abuseCreated = abuseCreated;
    }

    public long getAbuseExpires() {
        return this.abuseExpires;
    }

    public void setAbuseExpires(long abuseExpires) {
        this.abuseExpires = abuseExpires;
    }

}
