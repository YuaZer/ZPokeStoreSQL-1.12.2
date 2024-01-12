package io.github.yuazer.zpokestoresql.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLDatabase {
    private String databaseName;
    private String url;
    private String username;
    private String password;
    private Connection connection;

    public MySQLDatabase(String databaseName, String ip, int port, String username, String password) {
        this.databaseName = databaseName;
        this.url = "jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?useSSL=false";
        this.username = username;
        this.password = password;
        this.connection = null;
    }

    public boolean connect() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDatabaseIfNotExists() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
            PreparedStatement statement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + databaseName);
            statement.executeUpdate();
            statement.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayerData(String uuid, byte[] partyStore, byte[] pcStore) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO player_data (uuid, party_store, pc_store) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE party_store = ?, pc_store = ?");
            statement.setString(1, uuid);
            statement.setBytes(2, partyStore);
            statement.setBytes(3, pcStore);
            statement.setBytes(4, partyStore);
            statement.setBytes(5, pcStore);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                byte[] partyStore = resultSet.getBytes("party_store");
                byte[] pcStore = resultSet.getBytes("pc_store");
                return new PlayerData(uuid, partyStore, pcStore);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class PlayerData {
        private String uuid;
        private byte[] partyStore;
        private byte[] pcStore;

        public PlayerData(String uuid, byte[] partyStore, byte[] pcStore) {
            this.uuid = uuid;
            this.partyStore = partyStore;
            this.pcStore = pcStore;
        }

        public String getUuid() {
            return uuid;
        }

        public byte[] getPartyStore() {
            return partyStore;
        }

        public byte[] getPcStore() {
            return pcStore;
        }
    }
}

