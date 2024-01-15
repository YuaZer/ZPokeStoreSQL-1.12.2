package io.github.yuazer.zpokestoresql.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import io.github.yuazer.zpokestoresql.Main;
import io.github.yuazer.zpokestoresql.database.MySQLDatabase;
import io.github.yuazer.zpokestoresql.utils.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class PlayerEvent implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent event) throws IOException {
        Player player = event.getPlayer();
        NBTTagCompound partyTag = new NBTTagCompound();
        NBTTagCompound pcTag = new NBTTagCompound();
        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(player.getUniqueId());
        PCStorage pcs = Pixelmon.storageManager.getPCForPlayer(player.getUniqueId());
        pps.writeToNBT(partyTag);
        pcs.writeToNBT(pcTag);
        Main.getDatabase().setPlayerData(player.getName(), NBTUtils.serializeNBT(partyTag), NBTUtils.serializeNBT(pcTag));
        Main.getInstance().getLogger().info("§b玩家:§a" + player.getName() + "§b宝可梦数据保存成功");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        PlayerPartyStorage pps = Pixelmon.storageManager.getParty(player.getUniqueId());
        MySQLDatabase.PlayerData playerData = Main.getDatabase().getPlayerData(player.getName());
        if (playerData==null){
            return;
        }
        NBTTagCompound partyTag = NBTUtils.deserializeNBT(playerData.getPartyStore());
        NBTTagCompound pcTag = NBTUtils.deserializeNBT(playerData.getPcStore());
        pps.readFromNBT(partyTag);
        PCStorage pcs = Pixelmon.storageManager.getPCForPlayer(player.getUniqueId());
        pcs.readFromNBT(pcTag);
        Main.getInstance().getLogger().info("§b玩家:§a" + player.getName() + "§b宝可梦数据读取成功");
    }
}
