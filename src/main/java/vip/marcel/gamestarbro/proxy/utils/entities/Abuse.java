package vip.marcel.gamestarbro.proxy.utils.entities;

import java.util.List;

public class Abuse {

    private String abuseReason;
    private String abusePermissionNeed;
    private int abuseId;

    private List<String> abuseDurations;

    public Abuse() {
    }

    public String getAbuseReason() {
        return this.abuseReason;
    }

    public void setAbuseReason(String abuseReason) {
        this.abuseReason = abuseReason;
    }

    public String getAbusePermissionNeed() {
        return this.abusePermissionNeed;
    }

    public void setAbusePermissionNeed(String abusePermissionNeed) {
        this.abusePermissionNeed = abusePermissionNeed;
    }

    public int getAbuseId() {
        return this.abuseId;
    }

    public void setAbuseId(int abuseId) {
        this.abuseId = abuseId;
    }

    public List<String> getAbuseDurations() {
        return this.abuseDurations;
    }

    public void setAbuseDurations(List<String> abuseDurations) {
        this.abuseDurations = abuseDurations;
    }

}
