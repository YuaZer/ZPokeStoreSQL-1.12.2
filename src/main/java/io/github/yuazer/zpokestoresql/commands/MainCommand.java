package io.github.yuazer.zpokestoresql.commands;

import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import io.github.yuazer.zpokestoresql.Main;
import io.github.yuazer.zpokestoresql.database.MySQLDatabase;
import io.github.yuazer.zpokestoresql.utils.NBTUtils;
import io.github.yuazer.zpokestoresql.utils.YamlUtils;
import net.minecraft.nbt.CompoundNBT;
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
            } else if (args.length==2&&args[0].equalsIgnoreCase("load") && sender.isOp()) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("§c该玩家不在线!");
                    return true;
                }
                try {
                    PlayerPartyStorage pps = StorageProxy.getParty(player.getUniqueId());
                    MySQLDatabase.PlayerData playerData = Main.getDatabase().getPlayerData(player.getName());
                    if (playerData == null || playerData.getPcStore() == null || playerData.getPartyStore() == null) {
                        sender.sendMessage("§b"+player.getName()+"§e的数据不存在，跳过读取");
                        return true;
                    }
                    CompoundNBT partyTag = NBTUtils.deserializeNBT(playerData.getPartyStore());
                    CompoundNBT pcTag = NBTUtils.deserializeNBT(playerData.getPcStore());
                    pps.readFromNBT(partyTag);
                    PCStorage pcs = StorageProxy.getPCForPlayer(player.getUniqueId());
                    pcs.readFromNBT(pcTag);
                    Main.getInstance().getLogger().info("§b玩家:§a" + player.getName() + "§b宝可梦数据读取成功");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return false;
    }
}
