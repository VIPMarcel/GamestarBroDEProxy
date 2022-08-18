package vip.marcel.gamestarbro.proxy.utils.database;

import vip.marcel.gamestarbro.proxy.Proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySQL {

    private final Proxy plugin;

    private final String hostname, database, username, password;
    private final int port;

    private Connection connection;

    public MySQL(Proxy plugin) {
        this.plugin = plugin;

        this.hostname = plugin.getConfigManager().getConfiguration().getString("Database.MySQL.Hostname");
        this.database = plugin.getConfigManager().getConfiguration().getString("Database.MySQL.Database");
        this.username = plugin.getConfigManager().getConfiguration().getString("Database.MySQL.Username");
        this.password = plugin.getConfigManager().getConfiguration().getString("Database.MySQL.Password");
        this.port = plugin.getConfigManager().getConfiguration().getInt("Database.MySQL.Port");
    }

    public void connect() {
        try {
            if(this.connection == null) {
                this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password);
                this.plugin.getLogger().log(Level.INFO, "MySQL connection successfully opend!");
            }

            {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS AbuseBans(id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, AbusedBy TEXT, AbuseReason TEXT, AbuseId TEXT, AbuseCreated TEXT, AbuseEnd TEXT)");
                statement.executeUpdate();
                statement.close();
            }

            {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS AbuseMutes(id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, AbusedBy TEXT, AbuseReason TEXT, AbuseId TEXT, AbuseCreated TEXT, AbuseEnd TEXT)");
                statement.executeUpdate();
                statement.close();
            }

            {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS AllAbuseBans(id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, AbusedBy TEXT, AbuseReason TEXT, AbuseId TEXT, AbuseCreated TEXT, AbuseEnd TEXT)");
                statement.executeUpdate();
                statement.close();
            }

            {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS AllAbuseMutes(id INT AUTO_INCREMENT PRIMARY KEY, UUID TEXT, AbusedBy TEXT, AbuseReason TEXT, AbuseId TEXT, AbuseCreated TEXT, AbuseEnd TEXT)");
                statement.executeUpdate();
                statement.close();
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if(this.connection != null) {
                this.connection.close();
                this.plugin.getLogger().log(Level.INFO, "MySQL connection successfully closed!");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

}
