package io.github.yuazer.zpokestoresql;

import io.github.yuazer.zpokestoresql.commands.MainCommand;
import io.github.yuazer.zpokestoresql.database.MySQLDatabase;
import io.github.yuazer.zpokestoresql.listener.PlayerEvent;
import io.github.yuazer.zpokestoresql.utils.YamlUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }
    private static MySQLDatabase database;

    public static MySQLDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        String databaseName = YamlUtils.getConfigMessage("MySQLSetting.databaseName");
        String ip = YamlUtils.getConfigMessage("MySQLSetting.ip");
        int port = YamlUtils.getConfigInt("MySQLSetting.port");
        String username = YamlUtils.getConfigMessage("MySQLSetting.username");
        String password = YamlUtils.getConfigMessage("MySQLSetting.password");
        database = new MySQLDatabase(
                databaseName, ip, port, username, password);
        if (!database.connect()) {
            getLogger().severe("§c无法连接到数据库！");
            return;
        }
        // 创建数据库（如果不存在）
        if (!database.createDatabaseIfNotExists()) {
            getLogger().severe("§c无法创建数据库！");
            database.disconnect();
            return;
        }
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(),this);
        Bukkit.getPluginCommand("zpokestoresql").setExecutor(new MainCommand());
        logLoaded(this);
    }

    @Override
    public void onDisable() {
        logDisable(this);
    }
    public static void logLoaded(JavaPlugin plugin) {
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §f已加载", plugin.getName()));
        Bukkit.getLogger().info("§b作者:§eZ菌[QQ:1109132]");
        Bukkit.getLogger().info("§b版本:§e" + plugin.getDescription().getVersion());
    }

    public static void logDisable(JavaPlugin plugin) {
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §c已卸载", plugin.getName()));
    }
}
