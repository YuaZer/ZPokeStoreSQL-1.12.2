package io.github.yuazer.zpokestoresql.database;

import io.github.yuazer.zpokestoresql.Main;

import java.sql.*;

public class MySQLDatabase {
    private String databaseName;
    private String url;
    private String username;
    private String password;
    private Connection connection;
    private String urlOrginal;
    public MySQLDatabase(String databaseName, String ip, int port, String username, String password) {
        this.databaseName = databaseName;
        this.url = "jdbc:mysql://" + ip + ":" + port +"/"+ databaseName+"?useSSL=false";
        this.urlOrginal = "jdbc:mysql://" + ip + ":" + port +"?useSSL=false";
        this.username = username;
        this.password = password;
        this.connection = null;
        // 在初始化时检测数据库是否存在，如果不存在则创建
        if (!createDatabase()) {
            System.err.println("无法创建数据库 " + databaseName + "。");
        }
        if (createTable()) {
            System.out.println("§a检测表是否创建成功");
        } else {
            System.err.println("无法创建表 player_data。");
        }
        if (!connect()) {
            Main.getInstance().getLogger().severe("§c无法连接到数据库！");
        }
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

    // 其他方法不变...

    // 检测数据库是否存在
    private boolean checkDatabaseExists() {
        try {
            Connection conn = DriverManager.getConnection(urlOrginal, username, password);
            PreparedStatement statement = conn.prepareStatement("SHOW DATABASES LIKE ?");
            statement.setString(1, databaseName);
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            resultSet.close();
            statement.close();
            conn.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 创建数据库
    private boolean createDatabase() {
        if (checkDatabaseExists()) {
            return true; // 数据库已经存在，无需创建
        }
        try {
            Connection conn = DriverManager.getConnection(urlOrginal, username, password);
            PreparedStatement statement = conn.prepareStatement("CREATE DATABASE " + databaseName);
            statement.executeUpdate();
            statement.close();
            conn.close();
            System.out.println("§a数据库 §b" + databaseName + " §a创建成功。");
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
    private boolean createTable() {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            // 选择要执行操作的数据库
            Statement useDbStatement = conn.createStatement();
            useDbStatement.close();
            Statement statement = conn.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS player_data (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "party_store BLOB," +
                    "pc_store BLOB" +
                    ")";
            statement.executeUpdate(createTableSQL);
            statement.close();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

