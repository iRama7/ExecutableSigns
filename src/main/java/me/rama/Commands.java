package me.rama;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Commands implements TabExecutor {

    private final ExecutableSigns main;

    public Commands(ExecutableSigns main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command c, @NotNull String s, @NotNull String[] args) {

        if(sender instanceof ConsoleCommandSender){
            sender.sendMessage(main.colorized("&cYou can't use this command from console."));
            return false;
        }

        Player player = (Player) sender;

        // /sign attachCommand <executor> <command>

        if(args.length < 3){

            // /sign remove
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("remove")){
                    Block playerLookingBlock = player.getTargetBlockExact(3);

                    if(main.isSignBlock(playerLookingBlock)){
                        Sign sign = main.matchSign(playerLookingBlock);
                        if(sign != null){
                            main.removeSign(sign.getSign());
                            player.sendMessage(main.colorized("&aSign removed successfully."));
                        }else{
                            player.sendMessage(main.colorized("&eThere is no command attached to that sign."));
                        }
                    }else{
                        player.sendMessage(main.colorized("&eYou need to look at a sign."));
                    }

                }
            }else {
                player.sendMessage(main.colorized("&aCorrect format: &7/sign attachCommand <executor> <command>"));
            }

        }else{

            Block playerLookingBlock = player.getTargetBlockExact(3);

            if(main.isSignBlock(playerLookingBlock)) {

                if (main.matchSign(playerLookingBlock) == null) {

                    String executor = args[1];
                    String command = "";
                    int length = args.length;
                    for (int i = 2; i < length; i++) {
                        command = command + " " + args[i];
                    }

                    Sign sign = new Sign(command, executor, playerLookingBlock, main);
                    main.addSign(sign);
                    player.sendMessage(main.colorized("&aCommand added successfully."));

                }else{

                    player.sendMessage(main.colorized("&eThis sign already has a command attached."));
                    player.sendMessage(main.colorized("&eIf you want to remove the sign from the database use /sign remove."));
                    return false;

                }

            }else{

                player.sendMessage(main.colorized("&eYou need to look at a sign block!"));
                return false;

            }



        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if(sender.hasPermission("es.admin")) {
            if (args.length == 1) {
                commands.add("remove");
                commands.add("attachCommand");
                StringUtil.copyPartialMatches(args[0], commands, completions);
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("attachCommand")) {
                commands.add("CONSOLE");
                commands.add("PLAYER");
                StringUtil.copyPartialMatches(args[1], commands, completions);
                return completions;
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("attachCommand")) {
                commands.add("<command>");
                StringUtil.copyPartialMatches(args[2], commands, completions);
                return completions;
            }
        }

        return completions;

    }

}
