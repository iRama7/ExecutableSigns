package me.rama;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class Events implements Listener {

    private final ExecutableSigns main;

    public Events(ExecutableSigns main) {
        this.main = main;
    }

    @EventHandler
    public void interactSignEvent(PlayerInteractEvent event){
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if(block == null){
            return;
        }
        Material material = block.getType();
        Player player = event.getPlayer();

        if(action.isRightClick() && main.isSignBlock(material)){

            //Sign right click event
            Sign sign = main.matchSign(block);
            if(sign != null){
                sign.execute(player);
                event.setCancelled(true);
            }

        }

    }

}
