package me.rama;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sign {

    private final String command;
    private final String executor;
    private final Block sign;
    private final ExecutableSigns main;

    public Sign(String command, String executor, Block sign, ExecutableSigns main) {
        this.command = command;
        this.executor = executor;
        this.sign = sign;
        this.main = main;

    }

    public Block getSign() {
        return sign;
    }

    public void execute(Player player){

        CommandSender sender = null;
        String replacedCommand = command.replaceAll("%player%", player.getName());

        if(executor.equalsIgnoreCase("CONSOLE")){
            sender = Bukkit.getConsoleSender();
        } else if (executor.equalsIgnoreCase("PLAYER")) {
            sender = player;
        }

        if(sender == null){
            main.log("&4&lERROR &cCommand sender (&f" + executor + "&c) is null, must be either CONSOLE or PLAYER.", false);
            return;
        }

        Bukkit.dispatchCommand(sender, replacedCommand);

    }

    public String getLocationString(){

        return main.deserializeLocation(sign);

    }

    public String getCommand() {
        return command;
    }

    public String getExecutor() {
        return executor;
    }
}
