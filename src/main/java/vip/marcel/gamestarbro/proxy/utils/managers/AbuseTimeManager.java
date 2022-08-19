package vip.marcel.gamestarbro.proxy.utils.managers;

import vip.marcel.gamestarbro.proxy.Proxy;

public record AbuseTimeManager(Proxy plugin) {

    public String getTempBanString(String timeString) {
        String timeFormat = timeString.split(" ")[1];
        int endOfBan = 0;

        try {
            endOfBan = Integer.valueOf(timeString.split(" ")[0]);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        if(timeFormat.equalsIgnoreCase("s")) {
            if(endOfBan != 1) {
                timeFormat = timeFormat.replaceAll("s", "Sekunden");
            }
            else {
                timeFormat = timeFormat.replaceAll("s", "Sekunde");
            }
        }

        if(timeFormat.equalsIgnoreCase("m")) {
            if(endOfBan != 1) {
                timeFormat = timeFormat.replaceAll("m", "Minuten");
            }
            else {
                timeFormat = timeFormat.replaceAll("m", "Minute");
            }
        }

        if(timeFormat.equalsIgnoreCase("h")) {
            if(endOfBan != 1) {
                timeFormat = timeFormat.replaceAll("h", "Stunden");
            }
            else {
                timeFormat = timeFormat.replaceAll("h", "Stunde");
            }
        }

        if(timeFormat.equalsIgnoreCase("d")) {
            if(endOfBan != 1) {
                timeFormat = timeFormat.replaceAll("d", "Tage");
            }
            else {
                timeFormat = timeFormat.replaceAll("d", "Tag");
            }
        }

        return endOfBan + " " + timeFormat;
    }

    public String getTempBanLength(long endOfBan) {
        String message = "";
        long now = System.currentTimeMillis();
        long diff = endOfBan - now;
        long seconds = (diff / 1000L);

        if(seconds >= 86400) {
            long days = seconds / 86400;
            seconds %= 86400;
            if(days != 1) {
                message = message + days + " Tage";
            }
            else {
                message = message + days + " Tag";
            }
        }

        if(seconds >= 3600) {
            long hours = seconds / 3600;
            seconds %= 3600;
            if(hours != 1) {
                if(message != "") {
                    message = message + ", " + hours + " Stunden";
                }
                else {
                    message = message + hours + " Stunden";
                }
            }
            else {
                if(message != "") {
                    message = message + ", " + hours + " Stunde";
                }
                else {
                    message = message + hours + " Stunde";
                }
            }
        }

        if(seconds >= 60) {
            long minutes = seconds / 60;
            seconds %= 60;
            if(minutes != 1) {
                if(message != "") {
                    message = message + ", " + minutes + " Minuten";
                }
                else {
                    message = message + minutes + " Minuten";
                }
            }
            else {
                if(message != "") {
                    message = message + ", " + minutes + " Minute";
                }
                else {
                    message = message + minutes + " Minute";
                }
            }
        }

        if(seconds > 0) {
            if(seconds != 1) {
                if(message != "") {
                    message = message + ", " + seconds + " Sekunden";
                }
                else {
                    message = message + seconds + " Sekunden";
                }
            }
            else {
                if(message != "") {
                    message = message + ", " + seconds + " Sekunde";
                }
                else {
                    message = message + seconds + " Sekunde";
                }
            }
        }

        return message;
    }

    public long getTimeInMillis(String[] timeString) {
        long time = 0;

        if(timeString[1].equalsIgnoreCase("s")) {
            try {
                long time2 = Long.valueOf(timeString[0]);
                time = time2 * 1000;
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        if(timeString[1].equalsIgnoreCase("m")) {
            try {
                long time2 = Long.valueOf(timeString[0]);
                time = time2 * 1000 * 60;
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        if(timeString[1].equalsIgnoreCase("h")) {
            try {
                long time2 = Long.valueOf(timeString[0]);
                time = time2 * 1000 * 60 * 60;
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        if(timeString[1].equalsIgnoreCase("d")) {
            try {
                long time2 = Long.valueOf(timeString[0]);
                time = time2 * 1000 * 60 * 60 * 24;
            } catch(NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        return time;
    }

    public String getSimpleTimeString(long seconds) {
        String time;

        if(seconds == 1) {
            time = "1 Sekunde";
        } else if(seconds < 60) {
            time = seconds + " Sekunden";
        } else if(seconds >= 60 && seconds < 120) {
            time = "1 Minute";
        }  else if(seconds >= (60 * 2) && seconds < (60 * 60)) {
            time = seconds / 60 + " Minuten";
        }  else if(seconds >= (60 * 60 * 1) && seconds < (60 * 60 * 2)) {
            time = seconds / 60 / 60 + " Stunde";
        }  else if(seconds >= (60 * 60 * 2) && seconds < (60 * 60 * 24)) {
            time = seconds / 60 / 60 + " Stunden";
        }  else if(seconds >= (60 * 60 * 24) && seconds < (60 * 60 * (24 * 2))) {
            time = seconds / 60 / 60 / 24 + " Tag";
        } else {
            time = seconds / 60 / 60 / 24 + " Tage";
        }

        return time;
    }


}
