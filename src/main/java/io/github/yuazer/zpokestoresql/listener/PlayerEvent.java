package io.github.yuazer.zpokestoresql.listener;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.storage.PCStorage;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenScreenPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientChangeOpenPCPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.clientStorage.newStorage.pc.ClientInitializePCPacket;
import com.pixelmonmod.pixelmon.enums.EnumGuiScreen;
import io.github.yuazer.zpokestoresql.Main;
import io.github.yuazer.zpokestoresql.database.MySQLDatabase;
import io.github.yuazer.zpokestoresql.utils.NBTUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.bukkit.Bukkit;
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
        CompoundNBT partyTag = new CompoundNBT();
        CompoundNBT pcTag = new CompoundNBT();
        PlayerPartyStorage pps = StorageProxy.getParty(player.getUniqueId());
        PCStorage pcs = StorageProxy.getPCForPlayer(player.getUniqueId());
        pps.writeToNBT(partyTag);
        pcs.writeToNBT(pcTag);
//        Main.getDatabase().setPlayerData(player.getUniqueId().toString(), NBTUtils.serializeNBT(partyTag), NBTUtils.serializeNBT(pcTag));
        Main.getDatabase().setPlayerData(player.getName(), NBTUtils.serializeNBT(partyTag), NBTUtils.serializeNBT(pcTag));
        Main.getInstance().getLogger().info("§b玩家:§a" + player.getName() + "§b宝可梦数据保存成功");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        PlayerPartyStorage pps = StorageProxy.getParty(player.getUniqueId());
//        MySQLDatabase.PlayerData playerData = Main.getDatabase().getPlayerData(player.getUniqueId().toString());
        MySQLDatabase.PlayerData playerData = Main.getDatabase().getPlayerData(player.getName());
        if (playerData==null||playerData.getPcStore()==null||playerData.getPartyStore()==null){
            return;
        }
        CompoundNBT partyTag = NBTUtils.deserializeNBT(playerData.getPartyStore());
        CompoundNBT pcTag = NBTUtils.deserializeNBT(playerData.getPcStore());
        pps.readFromNBT(partyTag);
        PCStorage pcs = StorageProxy.getPCForPlayer(player.getUniqueId());
        pcs.readFromNBT(pcTag);
        Main.getInstance().getLogger().info("§b玩家:§a" + player.getName() + "§b宝可梦数据读取成功");
        Bukkit.getScheduler().runTask(Main.getInstance() ,() -> {
            ServerPlayerEntity entityPlayerMP = StorageProxy.getParty(player.getUniqueId()).getPlayer();
            PCStorage pCStorage = StorageProxy.getPC(entityPlayerMP, null);
            Pixelmon.network.sendToServer(new ClientInitializePCPacket(pCStorage));
            Pixelmon.network.sendToServer(new ClientChangeOpenPCPacket(pCStorage.uuid));
            pCStorage.sendContents(entityPlayerMP);
            OpenScreenPacket.open(entityPlayerMP, EnumGuiScreen.PC);
            player.openInventory(player.getInventory());
            player.closeInventory();
        });
    }
}
