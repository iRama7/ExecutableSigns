package me.rama;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.getConsoleSender;

public final class ExecutableSigns extends JavaPlugin {

    private Database database;
    private List<Sign> signs;


    @Override
    public void onEnable() {
        log("&aEnabling plugin...", false);
        initDatabase();
        log("&aLoaded &c" + loadSigns() + " &asigns from database.", false);
        registerCommands();
        registerEvents();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void log(String message, boolean isDebug) {

        String prefix = colorized("&1[&bExecutable Signs&1] &r");
        message = colorized(message);

        if (isDebug) {
            prefix = colorized("&1[&bExecutable Signs&1] &e[DEBUG] &r");
            getConsoleSender().sendMessage(prefix + message);
        } else {
            getConsoleSender().sendMessage(prefix + colorized(message));
        }
    }

    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    public void registerCommands(){
        TabExecutor Commands = new Commands(this);
        this.getCommand("sign").setExecutor(Commands);
        this.getCommand("sign").setTabCompleter(Commands);
    }

    public String colorized(String s) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String hexCode = s.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            s = s.replace(hexCode, builder.toString());
            matcher = pattern.matcher(s);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private int loadSigns(){
        try {
            signs = database.getSigns();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return signs.size();
    }

    private void initDatabase(){

        File f = getDataFolder();
        if(!f.exists()){
            f.mkdirs();
        }

        log("&eInitializing &fSQLite &edatabase...", false);
        try {
            database = new Database(this, getDataFolder().getAbsolutePath() + "/signs.db");
            log("&aDatabase created successfully.", false);
        } catch (SQLException e) {
            log("&4[ERROR] &cCould not create the database: &f" + e.getMessage(), false);
            getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

    }

    public Database getDatabase() {
        return database;
    }

    public boolean isSignBlock(Material material){
        String material_name = material.toString();
        return material_name.contains("SIGN");
    }

    public boolean isSignBlock(Block block){
        if(block == null){
            return false;
        }
        String material_name = block.getType().toString();
        return material_name.contains("SIGN");
    }

    public Sign matchSign(Block block){
        Sign s = null;
        for(Sign sign : signs){
            if(sign.getSign().equals(block)){
                s = sign;
            }
        }
        return s;
    }

    public void addSign(Sign sign){
        signs.add(sign);
        try {
            database.addSign(sign);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeSign(Block sign_block){

        String location_string = deserializeLocation(sign_block);
        Sign sign = getSign(location_string);
        if(sign != null){
            signs.remove(sign);
            try {
                database.removeSign(sign);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public Sign getSign(String locationString){
        Sign s = null;

        for(Sign sign : signs){
            if(sign.getLocationString().equalsIgnoreCase(locationString)){
                s = sign;
            }
        }

        return s;

    }

    public String deserializeLocation(Block block){
        //world;x;y;z - spawn;105;45;123

        String world_name = block.getWorld().getName();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String location_string = world_name + ";" + x + ";" + y + ";" + z;

        return location_string;

    }

    public Block serializeLocation(String location_string){
        //world;x;y;z - spawn;105.5;45;123.5
        String split[] = location_string.split(";");
        String world_name = split[0];

        World world = Bukkit.getWorld(world_name);
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[2]);
        int z = Integer.parseInt(split[3]);

        Block block = world.getBlockAt(x, y, z);

        return block;
    }

}
