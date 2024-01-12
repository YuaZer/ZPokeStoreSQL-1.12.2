package io.github.yuazer.zpokestoresql.utils;



import io.github.yuazer.zpokestoresql.Main;

import java.util.List;

public class YamlUtils {
    public static String getConfigMessage(String path) {
        if (Main.getInstance().getConfig().getString(path) != null && !Main.getInstance().getConfig().getString(path).isEmpty()) {
            return Main.getInstance().getConfig().getString(path).replace("&", "§");
        }
        return "这是空的消息,请检查配置文件"+path;
    }

    public static List<String> getConfigStringList(String path) {
        return Main.getInstance().getConfig().getStringList(path);
    }
    public static List<Integer> getConfigIntegerList(String path) {
        return Main.getInstance().getConfig().getIntegerList(path);
    }
    public static boolean getConfigBoolean(String path) {
        return Main.getInstance().getConfig().getBoolean(path);
    }

    public static int getConfigInt(String path) {
        return Main.getInstance().getConfig().getInt(path);
    }

    public static double getConfigDouble(String path) {
        return Main.getInstance().getConfig().getDouble(path);
    }
}
