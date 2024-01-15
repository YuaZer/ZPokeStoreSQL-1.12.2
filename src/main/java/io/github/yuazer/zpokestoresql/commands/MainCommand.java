package io.github.yuazer.zpokestoresql.commands;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.yuazer.zpokestoresql.Main;
import io.github.yuazer.zpokestoresql.database.MySQLDatabase;
import io.github.yuazer.zpokestoresql.utils.NBTUtils;
import io.github.yuazer.zpokestoresql.utils.YamlUtils;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("zpokestoresql")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("reload") && sender.isOp()) {
                Main.getInstance().reloadConfig();
                sender.sendMessage(YamlUtils.getConfigMessage("Message.reload"));
            } else if (args.length == 2 && args[0].equalsIgnoreCase("load") && sender.isOp()) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("§c该玩家不在线!");
                    return true;
                }
                try {
                    PlayerPartyStorage pps = Pixelmon.storageManager.getParty(player.getUniqueId());
                    MySQLDatabase.PlayerData playerData = Main.getDatabase().getPlayerData(player.getName());
                    if (playerData == null) {
                        sender.sendMessage("§b" + player.getName() + "§e的数据不存在，跳过读取");
                        return true;
                    }
                    NBTTagCompound partyTag = NBTUtils.deserializeNBT(playerData.getPartyStore());
                    NBTTagCompound pcTag = NBTUtils.deserializeNBT(playerData.getPcStore());
                    pps.readFromNBT(partyTag);
                    PCStorage pcs = Pixelmon.storageManager.getPCForPlayer(player.getUniqueId());
                    pcs.readFromNBT(pcTag);
                    Main.getInstance().getLogger().info("§b玩家:§a" + player.getName() + "§b宝可梦数据读取成功");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
