package io.github.yuazer.zpokestoresql.commands;

import io.github.yuazer.zpokestoresql.Main;
import io.github.yuazer.zpokestoresql.utils.YamlUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("zpokestoresql")){
            if (args.length==0||args[0].equalsIgnoreCase("reload")&&sender.isOp()){
                Main.getInstance().reloadConfig();
                sender.sendMessage(YamlUtils.getConfigMessage("Message.reload"));
            }
            return true;
        }
        return false;
    }
}
